package com.manhtran.finaltrainning.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "BOOKINGS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_date")
    private LocalDate bookingFromDate = LocalDate.now();

    @Column(name = "to_date")
    private LocalDate bookingToDate = LocalDate.now();

    @Column(name = "room_price")
    private Double roomPrice = 0.0;

    @Column(name = "total_price")
    private Double totalPrice = 0.0;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "room_id")
    private RoomEntity roomEntity;
}
