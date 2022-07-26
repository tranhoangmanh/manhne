package com.manhtran.finaltrainning.config;

import com.manhtran.finaltrainning.dtos.UserDTO;
import com.manhtran.finaltrainning.entities.RoleEntity;
import com.manhtran.finaltrainning.entities.UserEntity;
import com.manhtran.finaltrainning.repositories.IRoleRepository;
import com.manhtran.finaltrainning.repositories.IUserRepository;
import com.manhtran.finaltrainning.services.UserService;
import com.manhtran.finaltrainning.utils.UserDetailsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    IRoleRepository roleRepository;

    @Autowired
    IUserRepository userRepository;

    @Autowired
    UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder());
    }

    @EventListener(classes = InteractiveAuthenticationSuccessEvent.class)
    public void authenticationSuccessEvent(InteractiveAuthenticationSuccessEvent event) {
        Authentication auth = event.getAuthentication();
        HttpSession session = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.currentRequestAttributes()))
                .getRequest().getSession();

        UserEntity userEntity = ((UserDetailsUtil) auth.getPrincipal()).getUserEntity();
        List<RoleEntity> listRoles = userEntity.getRoleEntityList();
        List<Integer> rolePriorityList = listRoles.stream().map(RoleEntity::getRolePriority).
                sorted(new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o2 - o1;
                    }
                }).toList();

        UserDTO userDTO = new UserDTO();
        userDTO.setId(userEntity.getId());
        userDTO.setUsername(userEntity.getUsername());
        if (rolePriorityList.get(0) == 1) {
            userDTO.setMaxRole("ADMIN");
        } else {
            userDTO.setMaxRole("USER");
            session.setAttribute("userLogin", userDTO);
        }
        session.setAttribute("userLogin", userDTO);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Chống tấn công vét cạn
        http.csrf().disable();

        // Lưu phiên đăng nhập - Remember me
        http.rememberMe().key("uniqueAndSecret").tokenValiditySeconds(1296000);

        //Kiểm tra quyền cho từng đường dẫn
        //Chỉ Admin mới được truy cập
        http.authorizeRequests().antMatchers("/admin/**").hasAuthority("ADMIN")
                .antMatchers("/user/**").hasAnyAuthority("USER", "ADMIN");

        //Nếu như người dùng vào trang không được cấp quyền
        http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/no-permission");

        //LUÔN LUÔN CẤU HÌNH ĐOẠN CODE TRÊN TRƯỚC


        http.authorizeRequests()
                .antMatchers("/", "/home", "/checkAuthen", "/fake-data",
                        "/no-permission", "/assets/**","/css/**","/fonts/**",
                        "/images/**","/js/**", "/scss/**", "/page/**", "/cart/**", "/place-order").permitAll() // Không cần đăng nhập
                // Còn đâu phải đăng nhập
                .anyRequest()
                .authenticated()
                .and()
                .formLogin() //Trang login mặc định
                .loginPage("/custom-login") //Tuỳ chỉnh trang login
                .loginProcessingUrl("/login") //Action từ form bắn lên
                //Cấu hình khi đăng nhập thành công
                .successHandler(new AuthenticationSuccessHandler() {
                                    @Override
                                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                        String username = request.getParameter("username");
                                        Optional<UserEntity> userEntityOptional = userRepository.findUserEntityByUsername(username);
                                        if (userEntityOptional.isEmpty()) { // Không tìm được người dùng
                                            throw new ServletException();
                                        } else {
                                            UserEntity userEntity = userEntityOptional.get();
                                            UserDetailsUtil customUser = (UserDetailsUtil) authentication.getPrincipal();
                                            List<RoleEntity> listRoles = customUser.getUserEntity().getRoleEntityList();
                                            List<Integer> rolePriorityList = listRoles.stream().map(RoleEntity::getRolePriority)
                                                    .sorted(new Comparator<Integer>() {
                                                        @Override
                                                        public int compare(Integer o1, Integer o2) {
                                                            return o2 - o1;
                                                        }
                                                    }).toList();
                                            HttpSession session = request.getSession();
                                            UserDTO userDTO = new UserDTO();
                                            userDTO.setId(userEntity.getId());
                                            userDTO.setUsername(userEntity.getUsername());
                                            if (rolePriorityList.get(0) == 1) {
                                                userDTO.setMaxRole("ADMIN");
                                                session.setAttribute("userLogin", userDTO);
                                                response.sendRedirect("/admin");
                                            } else {
                                                userDTO.setMaxRole("USER");
                                                session.setAttribute("userLogin", userDTO);
                                                response.sendRedirect("/user");
                                            }
                                        }

                                    }
                                }
                ).failureHandler((request, response, exception) -> {
                    String username = request.getParameter("username");
                    Optional<UserEntity> userEntityOptional = userRepository.findUserEntityByUsername(username);
                    if (userEntityOptional.isEmpty()) {
                        response.setContentType("text/html");
                        response.setCharacterEncoding("UTF-8");
                        PrintWriter out = response.getWriter();
                        out.println("<h1>Tên đăng nhập hoặc mật khẩu không đúng!</h1><br>");
                        throw new ServletException();
                    }
                    response.sendRedirect("/custom-login");
                }).permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .permitAll();
    }
}

