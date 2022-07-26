package com.manhtran.finaltrainning.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderDTO {

    private List<OrderItemDTO> itemDTOs;

}
