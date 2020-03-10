package com.example.servicesondemand.model;

import java.io.Serializable;

public class User implements Serializable {
    private String category, email, firstName, id, image, lastName, phone;
    private int perHourCharge, type;
    private double rating;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getPerHourCharge() {
        return perHourCharge;
    }

    public void setPerHourCharge(Integer perHourCharge) {
        this.perHourCharge = perHourCharge;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public User(String category, String email, String firstName, String id, String image, String lastName, String phone, Integer perHourCharge, Integer type, double rating) {
        this.category = category;
        this.email = email;
        this.firstName = firstName;
        this.id = id;
        this.image = image;
        this.lastName = lastName;
        this.phone = phone;
        this.perHourCharge = perHourCharge;
        this.type = type;
        this.rating = rating;
    }

    public User() {
    }
}
