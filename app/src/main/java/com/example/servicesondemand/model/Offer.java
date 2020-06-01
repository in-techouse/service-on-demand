package com.example.servicesondemand.model;

import java.io.Serializable;

public class Offer implements Serializable {
    private String id, workerId, userId, jobId, timeRequired, description, status;
    private int budgetOffered;
    public Offer() {
    }

    public Offer(String id, String workerId, String userId, String jobId, String timeRequired, String description, String status, int budgetOffered) {
        this.id = id;
        this.workerId = workerId;
        this.userId = userId;
        this.jobId = jobId;
        this.timeRequired = timeRequired;
        this.description = description;
        this.status = status;
        this.budgetOffered = budgetOffered;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getTimeRequired() {
        return timeRequired;
    }

    public void setTimeRequired(String timeRequired) {
        this.timeRequired = timeRequired;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getBudgetOffered() {
        return budgetOffered;
    }

    public void setBudgetOffered(int budgetOffered) {
        this.budgetOffered = budgetOffered;
    }
}
