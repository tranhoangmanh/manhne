package com.manhtran.finaltrainning.repositories;

import com.manhtran.finaltrainning.entities.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBookingRepository extends JpaRepository<BookingEntity, Long> {
}
