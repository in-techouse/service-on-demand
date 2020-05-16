package com.example.servicesondemand.model;

import java.io.Serializable;
import java.util.List;

public class Post implements Serializable {
    private String id, userId, workerId, status, date, time, address, description;
    private List<String> images;
    private double latitude, longitude;

    public Post() {
    }

    public Post(String id, String userId, String workerId, String status, String date, String time, String address, String description, List<String> images, double latitude, double longitude) {
        this.id = id;
        this.userId = userId;
        this.workerId = workerId;
        this.status = status;
        this.date = date;
        this.time = time;
        this.address = address;
        this.description = description;
        this.images = images;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
