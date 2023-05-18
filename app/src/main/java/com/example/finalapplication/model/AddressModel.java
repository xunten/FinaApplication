package com.example.finalapplication.model;

public class AddressModel {

    String userAddress;

    int totalPrice;
    boolean isSelected;

    public AddressModel() {
    }

    public AddressModel(String userAddress, boolean isSelected, int totalPrice) {
        this.userAddress = userAddress;
        this.isSelected = isSelected;
        this.totalPrice = totalPrice;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
