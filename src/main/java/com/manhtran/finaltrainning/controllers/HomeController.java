package com.manhtran.finaltrainning.controllers;

import com.manhtran.finaltrainning.dtos.CartDTO;
import com.manhtran.finaltrainning.dtos.CartItemDTO;
import com.manhtran.finaltrainning.dtos.RoomDTO;
import com.manhtran.finaltrainning.dtos.UserDTO;
import com.manhtran.finaltrainning.entities.RoleEntity;
import com.manhtran.finaltrainning.entities.RoomEntity;
import com.manhtran.finaltrainning.entities.UserEntity;
import com.manhtran.finaltrainning.repositories.IRoomRepository;
import com.manhtran.finaltrainning.repositories.IUserRepository;
import com.manhtran.finaltrainning.services.CartService;
import com.manhtran.finaltrainning.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class HomeController {
    @Autowired
    IUserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    IRoomRepository roomRepository;
    @Autowired
    RoomService roomService;
    @Autowired
    CartService cartService;
    @Value("${config.upload}")
    String UPLOAD_FOLDER;

    @GetMapping(value = "/custom-login")
    public String customLoginPage() {
        return "login_page";
    }

    @GetMapping("/admin")
    public String adminPage(HttpSession session, Model model) {
        UserDTO userDTO = (UserDTO) session.getAttribute("userLogin");
        model.addAttribute("userDTO", userDTO);
        List<RoomEntity> allRooms = roomRepository.findAll();
        model.addAttribute("allRooms", allRooms);
        model.addAttribute("roomDTO", new RoomDTO());
        RoomDTO roomDTO = new RoomDTO();
        return "admin_manage";
    }

    @GetMapping("/admin/delete")
    public String deleteProduct(@RequestParam("id") Long id) {
        roomRepository.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/insert_update")
    public String insertUpdate(@RequestParam("id") Long id, Model model) {
        if (id == 000) {
            model.addAttribute("p_iu", new RoomDTO());
        } else {
            model.addAttribute("p_iu", roomRepository.findById(id));
        }
        return "insert_update";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("imgFile") MultipartFile file,
                             RoomDTO roomDTO) {
        String relativeFilePath = "";
        LocalDate localDate = LocalDate.now();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        String subFolder = year +"_"+ month;
        String fullUploadDir = UPLOAD_FOLDER + subFolder;
        File checkDir = new File(fullUploadDir);
        try {
            relativeFilePath = subFolder + Instant.now().getEpochSecond() + file.getOriginalFilename();
            Files.write(Paths.get(UPLOAD_FOLDER + "//"+ relativeFilePath), file.getBytes());
        } catch (Exception ex) {
            System.out.println("Không thể tải file lên!");
            ex.printStackTrace();
            return null;
        }

        //Xử lý đối tượng trả về
        String imgName = relativeFilePath;
        if(roomDTO.getId() != null){
            Optional<RoomEntity> roomEntityOptional = roomRepository.findById(roomDTO.getId());
            if(roomEntityOptional.isPresent()){
                if(file.isEmpty()){
                    imgName = roomEntityOptional.get().getRoomImage();
                }
            }
        }
        roomDTO.setRoomImage(imgName);

        roomRepository.save(RoomDTO.dtoToEntity(roomDTO));
        return "redirect:/admin";
    }

    @PostMapping("/search")
    public String findCategory(RoomDTO roomDTO, Model model){
        List<RoomEntity> findByRoomName = roomRepository.findAllByRoomNameContainsIgnoreCase(roomDTO.getRoomName());
        if(findByRoomName.size() == 0) {
            model.addAttribute("allRooms", roomRepository.findAll());
        }
        model.addAttribute("allRooms", findByRoomName);
        return "redirect:/admin";
    }

    @GetMapping(value = {"/user", "/"})
    public String userPage(HttpSession session, Model model) {
        UserDTO userDTO = (UserDTO) session.getAttribute("userLogin");
        model.addAttribute("userDTO", userDTO);
        List<RoomEntity> allRooms = roomRepository.findAllByRoomRented(false);
        model.addAttribute("allRooms", allRooms);
        model.addAttribute("userResponse", userDTO);
        model.addAttribute("countProductInCart", 0);
        Page<RoomEntity> page = roomService.paginationProducts(1, 3);
        model.addAttribute("roomPage", page);
        List<Integer> pageNumbers = IntStream.rangeClosed(1, page.getTotalPages()).boxed().collect(Collectors.toList());
        model.addAttribute("pageNumbers", pageNumbers);
        return "index";
    }

    @GetMapping(value = "/place-order")
    public String placedOrder(HttpSession session){
        CartDTO cartDTO = (CartDTO) session.getAttribute("gioHang");
        if(cartDTO != null){
            for(CartItemDTO item : cartDTO.getItems()){
                RoomEntity roomEntity = roomRepository.findById(item.getRoomDTO().getId()).get();
                roomEntity.setRoomRented(true);
                roomRepository.save(roomEntity);
            }
        }
        return "redirect:/";
    }

    @GetMapping("/cart")
    public String cart(HttpSession session){
        CartDTO cart = cartService.getCart(session);
        if(cart.getItemCount() == 0){
            session.setAttribute("gioHang", cart);
        }
        return "cart";
    }

    @GetMapping("/cart/add")
    public String add(HttpSession session,
                      @RequestParam("id") Long id,
                      @RequestParam(value = "qty", required = false,
                              defaultValue = "1") int qty)
    {
        RoomEntity roomEntity = roomRepository.findById(id).get();
        CartDTO cart = cartService.getCart(session);
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(roomEntity.getId());
        roomDTO.setRoomName(roomEntity.getRoomName());
        roomDTO.setRoomImage(roomEntity.getRoomImage());
        roomDTO.setRoomPrice(roomEntity.getRoomPrice());
        cart.addItem(roomDTO, qty);
        session.setAttribute("gioHang", cart);
        return "cart";
    }

    @GetMapping("/cart/remove")
    public String remove(HttpSession session,
                         @RequestParam("id") RoomDTO roomDTO){
        CartDTO cart = cartService.getCart(session);
        cart.removeItem(roomDTO);
        return "cart";
    }

    @GetMapping("/cart/update")
    public String update(HttpSession session,
                         @RequestParam("id") Long id,
                         @RequestParam("qty") int qty){
        CartDTO cart = cartService.getCart(session);
        RoomEntity roomEntity = this.roomRepository.findById(id).get();
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(roomEntity.getId());
        roomDTO.setRoomName(roomEntity.getRoomName());
        roomDTO.setRoomImage(roomEntity.getRoomImage());
        roomDTO.setRoomPrice(roomEntity.getRoomPrice());
        cart.updateItem(roomDTO, qty);
        return "cart";
    }
    @GetMapping(value = "/no-permission")
    public String defaultErrorPaged() {
        return "default_error_page";
    }

    @GetMapping("/page")
    public ResponseEntity<?> pagination(@RequestParam(name = "pageNumber") Optional<Integer> page,
                                        @RequestParam(name = "pageSize") Optional<Integer> size){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(3);
        Page<RoomEntity> bookPage  = roomService.paginationProducts(currentPage, pageSize);

        List<RoomDTO> roomDTOList = bookPage.stream().map((room) -> RoomDTO.builder().id(room.getId())
                .roomName(room.getRoomName())
                .roomPrice(room.getRoomPrice())
                .roomImage(room.getRoomImage())
                .build()).collect(Collectors.toList());
        return ResponseEntity.ok().body(roomDTOList);
    }

    // Chạy một lần duy nhất để thêm dữ liệu tài khoản đăng nhập
    @GetMapping(value = "/fake-data")
    public String fakeData() {
        UserEntity admin = new UserEntity();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("manhne"));

        UserEntity user = new UserEntity();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("manhne"));

        RoleEntity userRole = new RoleEntity();
        userRole.setRoleName("USER");
        userRole.setRolePriority(0);

        RoleEntity adminRole = new RoleEntity();
        adminRole.setRoleName("ADMIN");
        adminRole.setRolePriority(1);

        List<RoleEntity> roleEntityListUser = new ArrayList<>();
        roleEntityListUser.add(userRole);

        List<RoleEntity> roleEntityListAdmin = new ArrayList<>();
        roleEntityListAdmin.add(userRole);
        roleEntityListAdmin.add(adminRole);

        admin.setRoleEntityList(roleEntityListAdmin);
        user.setRoleEntityList(roleEntityListUser);

        userRepository.save(admin);
        userRepository.save(user);

        return "redirect:/";
    }
}
