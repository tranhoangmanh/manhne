package com.manhtran.finaltrainning.repositories;

import com.manhtran.finaltrainning.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoleRepository extends JpaRepository<RoleEntity, Long> {
}
