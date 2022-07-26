package com.manhtran.finaltrainning.utils;

import com.manhtran.finaltrainning.entities.RoleEntity;
import com.manhtran.finaltrainning.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDetailsUtil implements UserDetails {
    private UserEntity userEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<RoleEntity> roleEntityList = userEntity.getRoleEntityList();
        List<Integer> listRolePriority = roleEntityList.stream().map(RoleEntity::getRolePriority).sorted(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        }).toList();
        if(listRolePriority.get(0) == 1)
        {
            return Collections.singleton(new SimpleGrantedAuthority("ADMIN"));
        }else {
            return Collections.singleton(new SimpleGrantedAuthority("USER"));
        }
    }

    @Override
    public String getPassword() {
        return this.userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return this.userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
