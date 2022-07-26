package com.manhtran.finaltrainning.repositories;

import com.manhtran.finaltrainning.dtos.RoomDTO;
import com.manhtran.finaltrainning.entities.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRoomRepository extends JpaRepository<RoomEntity, Long> {
    List<RoomEntity> findAllByRoomNameContainsIgnoreCase(String roomName);

    List<RoomEntity> findAllByRoomRented(boolean roomRented);
}
