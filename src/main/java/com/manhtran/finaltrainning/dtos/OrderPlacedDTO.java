package com.manhtran.finaltrainning.dtos;

import com.manhtran.finaltrainning.entities.BookingEntity;
import com.manhtran.finaltrainning.services.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderPlacedDTO {
    private long id;
    private double total;
    private String createdDate;
    private String address;
    private String phoneNumber;
    private long customerId;

    @Autowired
    private static UserService userService;

    public static BookingEntity dtoToEntity(OrderPlacedDTO dto){
        BookingEntity entity = new BookingEntity();
        entity.setId(dto.getId());
        entity.setBookingFromDate(LocalDate.now());
        entity.setBookingToDate(LocalDate.now().plusDays(10));
        entity.setRoomPrice(dto.getTotal());
        entity.setTotalPrice(dto.getTotal());
        return entity;
    }
}
