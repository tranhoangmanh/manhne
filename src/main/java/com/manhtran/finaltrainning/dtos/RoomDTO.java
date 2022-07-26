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
public class RoomDTO {
    private Long id;
    private String roomName;
    private String roomImage;
    private Double roomPrice;

    public static RoomEntity dtoToEntity(RoomDTO roomDTO){
        return RoomEntity.builder()
                .id(roomDTO.getId())
                .roomName(roomDTO.getRoomName())
                .roomImage(roomDTO.getRoomImage())
                .roomPrice(roomDTO.getRoomPrice())
                .build();
    }
}
