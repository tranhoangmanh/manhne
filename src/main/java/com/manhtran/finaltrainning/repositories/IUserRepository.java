package com.manhtran.finaltrainning.repositories;

import com.manhtran.finaltrainning.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findUserEntityByUsername(String username);
}
