package com.example.handymen;

public class Worker {
    public String name, email, phone, profession, experience, rate, location;

    public Worker() {} // Required for Firebase

    public Worker(String name, String email, String phone,
                  String profession, String experience, String rate, String location) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profession = profession;
        this.experience = experience;
        this.rate = rate;
        this.location = location;
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getProfession() { return profession; }
    public String getExperience() { return experience; }
    public String getRate() { return rate; }
    public String getLocation() { return location; }
}
/*
package com.example.handymen;

public class Worker {
    public String name;
    public String email;
    public String phone;
    public String profession;
    public String experience;
    public String rate;
    public String location;

    // Empty constructor needed for Firebase
    public Worker() {}

    // Constructor with fields
    public Worker(String name, String email, String phone,
                  String profession, String experience,
                  String rate, String location) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profession = profession;
        this.experience = experience;
        this.rate = rate;
        this.location = location;
    }
}

 */