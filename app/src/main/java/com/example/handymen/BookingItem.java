package com.example.handymen;

public class BookingItem {
    public String profession, name, email, phone, slot;

    public BookingItem() {}

    public BookingItem(String profession, String name, String email, String phone, String slot) {
        this.profession = profession;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.slot = slot;
    }
}
