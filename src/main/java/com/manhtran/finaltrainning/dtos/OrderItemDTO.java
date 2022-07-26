package com.manhtran.finaltrainning.dtos;

import com.manhtran.finaltrainning.entities.RoomEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderItemDTO {
    private int quantity;
    private double price;
    private RoomEntity roomEntity;
}
