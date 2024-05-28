package com.example.myapplication;

public class Counsellor {

    private String firstName, lastName, email;

    Counsellor(String inFirstName, String inLastName, String inEmail) {
        firstName = inFirstName;
        lastName = inLastName;
        email = inEmail;
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

    @Override
    public String toString() {
        return "Counsellor{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
