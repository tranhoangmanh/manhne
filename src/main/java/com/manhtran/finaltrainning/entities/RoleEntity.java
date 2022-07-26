package com.manhtran.finaltrainning.entities;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ROLES")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "role_priority")
    private int rolePriority = 0;

    @ManyToMany(mappedBy = "roleEntityList")
    private List<UserEntity> userEntityList;
}
