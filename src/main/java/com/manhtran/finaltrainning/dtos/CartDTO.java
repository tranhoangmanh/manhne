package com.manhtran.finaltrainning.dtos;

import java.util.ArrayList;
import java.util.List;

public class CartDTO {
    private List<CartItemDTO> items;
    private double total;

    public CartDTO() {
        this.items = new ArrayList<>();
        this.total = 0;
    }

    public CartItemDTO getItem(RoomDTO roomDTO){
        for(CartItemDTO item : items){
            if(item.getRoomDTO().getId().equals(roomDTO.getId())){
                return item;
            }
        }
        return null;
    }

    public List<CartItemDTO> getItems(){
        return items;
    }

    public int getItemCount(){
        return items.size();
    }

    public void addItem(CartItemDTO item){
        addItem(item.getRoomDTO(), item.getQuantity());
    }

    public void addItem(RoomDTO productDAO, int quantity){
        CartItemDTO item = getItem(productDAO);
        if(item != null){
            item.setQuantity(item.getQuantity() + quantity);
        }else {
            item = new CartItemDTO(productDAO);
            item.setQuantity(quantity);
            items.add(item);
        }
    }

    public void updateItem(RoomDTO roomDTO, int quantity){
        CartItemDTO item = getItem(roomDTO);
        if(item != null){
            item.setQuantity(quantity);
        }
    }

    public void removeItem(RoomDTO roomDTO){
        CartItemDTO item = getItem(roomDTO);
        if(item != null){
            items.remove(item);
        }
    }

    public void clear(){
        items.clear();
        total = 0;
    }

    public boolean isEmpty(){
        return items.isEmpty();
    }
}
