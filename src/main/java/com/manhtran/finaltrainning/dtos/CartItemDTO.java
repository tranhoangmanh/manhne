package com.manhtran.finaltrainning.dtos;

public class CartItemDTO {
    private RoomDTO roomDTO;
    private int quantity;
    private double subTotal;

    public CartItemDTO(RoomDTO roomDTO) {
        this.roomDTO = roomDTO;
        this.quantity = 1;
        this.subTotal = roomDTO.getRoomPrice();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubTotal() {
        subTotal = roomDTO.getRoomPrice() * this.quantity;
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public RoomDTO getRoomDTO() {
        return roomDTO;
    }
}
