package com.example.myapplication;

public class User {

    private String username, email, imageID;

    User(String inUser, String inEmail, String inImageID) {
        username = inUser;
        email = inEmail;
        imageID = inImageID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getImageID() {
        return imageID;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", imageID='" + imageID + '\'' +
                '}';
    }
}
