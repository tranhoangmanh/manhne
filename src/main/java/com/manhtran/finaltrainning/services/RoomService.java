package com.manhtran.finaltrainning.services;

import com.manhtran.finaltrainning.entities.RoomEntity;
import com.manhtran.finaltrainning.repositories.IRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
    @Autowired
    IRoomRepository roomRepository;

    public Page<RoomEntity> paginationProducts(int pageNumber, int pageSize){
        if(pageNumber == 0){
            pageNumber = 1;
        }
        PageRequest page = PageRequest.of(pageNumber - 1, pageSize);
        return roomRepository.findAll(page);
    }
}
