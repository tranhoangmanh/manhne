package com.manhtran.finaltrainning.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private long id;
    private String name;
    private long priority;
}
