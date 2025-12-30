package com.example.handymen;

public class Worker {

    public String name, email, phone, experience, rate, location, profession;

    public Worker() {}

    public Worker(String name, String email, String phone,
                  String experience, String rate,
                  String location, String profession) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.experience = experience;
        this.rate = rate;
        this.location = location;
        this.profession = profession;
    }
}

