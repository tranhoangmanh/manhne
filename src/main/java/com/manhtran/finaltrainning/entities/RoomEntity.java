package com.manhtran.finaltrainning.entities;

import lombok.*;
import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ROOMS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_name")
    private String roomName = "";

    @Column(name = "room_image")
    private String roomImage = "";

    @Column(name = "room_price")
    private Double roomPrice = 0.0;

    @Column(name = "room_rented")
    private Boolean roomRented = false;

    @OneToMany(mappedBy = "roomEntity", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private List<BookingEntity> bookingEntityList;
}
