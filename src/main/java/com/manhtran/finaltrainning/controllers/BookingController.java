package com.manhtran.finaltrainning.controllers;

import com.manhtran.finaltrainning.dtos.OrderDTO;
import com.manhtran.finaltrainning.dtos.OrderItemDTO;
import com.manhtran.finaltrainning.dtos.OrderPlacedDTO;
import com.manhtran.finaltrainning.dtos.UserDTO;
import com.manhtran.finaltrainning.entities.RoomEntity;
import com.manhtran.finaltrainning.entities.UserEntity;
import com.manhtran.finaltrainning.repositories.IBookingRepository;
import com.manhtran.finaltrainning.repositories.IRoomRepository;
import com.manhtran.finaltrainning.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BookingController {
        @Autowired
        IRoomRepository roomRepository;

        @Autowired
        IUserRepository userRepository;

        @Autowired
        IBookingRepository bookingRepository;


        @GetMapping("/cart")
        public String viewCart(HttpServletRequest request, HttpSession session){
            OrderDTO order = new OrderDTO(); // Chứa danh sách sản phẩm trong giỏ hàng
            if(session.getAttribute("cart") != null){
                order = (OrderDTO) session.getAttribute("cart");
                request.setAttribute("order", order);
                return "cart";
            }
            // Giỏ hàng chưa có sản phẩm nào
            return "redirect:/";
        }

        @GetMapping("/cart/add")
        public String add(HttpServletRequest request,
                          @RequestParam("id") Long id, HttpSession session) throws ServletException, IOException
        {
            RoomEntity room = roomRepository.findById(id).get();
            OrderDTO order = new OrderDTO();
            if(session.getAttribute("cart") == null){
                OrderItemDTO orderItem = new OrderItemDTO();
                orderItem.setQuantity(1);
                orderItem.setRoomEntity(room);
                orderItem.setPrice(room.getRoomPrice());

                List<OrderItemDTO> itemDTOS = new ArrayList<>();
                itemDTOS.add(orderItem);

                order.setItemDTOs(itemDTOS);
                session.setAttribute("cart", order);
                request.setAttribute("order", order);
            }else{
                order = (OrderDTO) session.getAttribute("cart");
                List<OrderItemDTO> itemDTOS = order.getItemDTOs();
                boolean flag = false;
                for(OrderItemDTO item : itemDTOS){
                    if(item.getRoomEntity().getId() == room.getId()){
                        item.setQuantity(item.getQuantity() + 1);
                        flag = true;
                    }
                }

                if(!flag){ //Nếu trong giỏ hàng chưa có sản phẩm này
                    OrderItemDTO orderItemDTO = new OrderItemDTO();
                    orderItemDTO.setRoomEntity(room);
                    orderItemDTO.setQuantity(1);
                    orderItemDTO.setPrice(room.getRoomPrice());
                    itemDTOS.add(orderItemDTO);
                }

                session.setAttribute("cart", order);
                request.setAttribute("order", order);
            }

            session.setAttribute("countProductInCart", order.getItemDTOs().size());
            double totalMoney = 0;
            for(OrderItemDTO item : order.getItemDTOs()){
                totalMoney += item.getQuantity() * item.getRoomEntity().getRoomPrice();
            }
            session.setAttribute("totalMoney", totalMoney);
            return "redirect:/";
        }

        @GetMapping("/cart/delete")
        public String delete(@RequestParam("id") Long id, HttpServletRequest request,
                             HttpSession session){
            Optional<RoomEntity> roomEntityOptional = roomRepository.findById(id);
            if(roomEntityOptional.isPresent()){
                RoomEntity roomEntity = roomEntityOptional.get();
                OrderDTO orderDTO = (OrderDTO) session.getAttribute("cart");
                List<OrderItemDTO> listOrderItemDTOs = orderDTO.getItemDTOs();
                for(OrderItemDTO x : listOrderItemDTOs){
                    if(Objects.equals(x.getRoomEntity().getId(), roomEntity.getId())){
                        listOrderItemDTOs.remove(x);
                        break;
                    }
                }

                if(orderDTO.getItemDTOs().size() > 0){
                    session.setAttribute("cart", orderDTO);
                    request.setAttribute("order", orderDTO);
                }else {
                    session.removeAttribute("cart");
                    request.setAttribute("order", orderDTO);
                }
            }
            return "cart";
        }

        //Đặt hàng
        @GetMapping(value = "/placed-order")
        public String placedOrder(HttpSession session, Model model){
            double totalMoney = (double) session.getAttribute("totalMoney");
            model.addAttribute("totalMoney", totalMoney);
            OrderDTO orderDTO = new OrderDTO();
            model.addAttribute("orderDTO", orderDTO);
            return "ordered_form";
            //return "ordered_successfully";
        }

        @PostMapping(value = "/submit-order")
        public String submitOrder(OrderPlacedDTO orderDTO, HttpSession session){
            double totalMoney = (double) session.getAttribute("totalMoney");
            orderDTO.setTotal(totalMoney);
            LocalDate date = LocalDate.now();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            orderDTO.setCreatedDate(dtf.format(date));
            UserDTO userResponse = (UserDTO) session.getAttribute("userResponse");
            UserEntity customerEntity = userRepository.findUserEntityByUsername(userResponse.getUsername()).get();
            orderDTO.setCustomerId(customerEntity.getId());
            bookingRepository.save(OrderPlacedDTO.dtoToEntity(orderDTO));
            session.setAttribute("orderDTO", orderDTO);
            return "redirect:/";
        }



}
