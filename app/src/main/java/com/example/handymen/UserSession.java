package com.example.handymen;

public class UserSession {

    private static String userId;
    private static String userLocation;

    public static void setUserId(String id) {
        userId = id;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserLocation(String location) {
        userLocation = location;
    }

    public static String getUserLocation() {
        return userLocation;
    }
}
