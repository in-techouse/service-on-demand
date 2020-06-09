package com.example.servicesondemand.model;

import java.io.Serializable;

public class Notification implements Serializable {
    private String id, userId, userText, workerId, workerText, jobId, dateTime;

    public Notification() {
    }

    public Notification(String id, String userId, String userText, String workerId, String workerText, String jobId, String dateTime) {
        this.id = id;
        this.userId = userId;
        this.userText = userText;
        this.workerId = workerId;
        this.workerText = workerText;
        this.jobId = jobId;
        this.dateTime = dateTime;
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

    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getWorkerText() {
        return workerText;
    }

    public void setWorkerText(String workerText) {
        this.workerText = workerText;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
