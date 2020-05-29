package com.example.servicesondemand.model;

import java.io.Serializable;

public class Offers implements Serializable {
    private String workerId, budgetOffered, timeRequired;

    public Offers() {
    }

    public Offers(String workerId, String budgetOffered, String timeRequired) {
        this.workerId = workerId;
        this.budgetOffered = budgetOffered;
        this.timeRequired = timeRequired;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getBudgetOffered() {
        return budgetOffered;
    }

    public void setBudgetOffered(String budgetOffered) {
        this.budgetOffered = budgetOffered;
    }

    public String getTimeRequired() {
        return timeRequired;
    }

    public void setTimeRequired(String timeRequired) {
        this.timeRequired = timeRequired;
    }
}
