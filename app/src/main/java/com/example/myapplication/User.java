package com.example.myapplication;

public class User {

    private String username, email, imageID;
    private int userID;

    User(String inUser, String inEmail, String inImageID, int inUserID) {
        username = inUser;
        email = inEmail;
        imageID = inImageID;
        userID = inUserID;
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

    public int getUserID() {
        return userID;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", imageID='" + imageID + '\'' +
                ", userID=" + userID +
                '}';
    }
}
