package com.example.servicesondemand.model;

import java.io.Serializable;

public class Complain implements Serializable {
    private String id, dateTime, userId, vendorId, jobId, complain;

    public Complain() {
    }

    public Complain(String id, String dateTime, String userId, String vendorId, String jobId, String complain) {
        this.id = id;
        this.dateTime = dateTime;
        this.userId = userId;
        this.vendorId = vendorId;
        this.jobId = jobId;
        this.complain = complain;
    }

    public String getComplain() {
        return complain;
    }

    public void setComplain(String complain) {
        this.complain = complain;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
