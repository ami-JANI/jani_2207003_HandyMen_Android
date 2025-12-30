package com.example.handymen;

public class User {

    public String name, email, phone, location;

    public User() {} // required for Firebase

    public User(String name, String email, String phone, String location) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.location = location;
    }
}
