package com.example.myapplication;

public class Counsellor {

    private String firstName, lastName, email;
    private int counsellorID;

    Counsellor(String inFirstName, String inLastName, String inEmail, int inCounsellorID) {
        firstName = inFirstName;
        lastName = inLastName;
        email = inEmail;
        counsellorID = inCounsellorID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public int getCounsellorID() {
        return counsellorID;
    }

    @Override
    public String toString() {
        return "Counsellor{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", counsellorID=" + counsellorID +
                '}';
    }
}
