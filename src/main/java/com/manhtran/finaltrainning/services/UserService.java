package com.manhtran.finaltrainning.services;

import com.manhtran.finaltrainning.entities.UserEntity;
import com.manhtran.finaltrainning.repositories.IUserRepository;
import com.manhtran.finaltrainning.utils.UserDetailsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntityOptional = userRepository.findUserEntityByUsername(username);
        if(userEntityOptional.isEmpty()){
            throw new UsernameNotFoundException("Tên đăng nhập hoặc mật khẩu không đúng!");
        }else {
            return new UserDetailsUtil(userEntityOptional.get());
        }
    }
}
