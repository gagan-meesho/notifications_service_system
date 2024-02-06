package com.assignment.notificationservice.entity.elasticsearch;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class SmsRequestIndex {
    private String id;
    private String phoneNumber;
    private String message;
    private String status;
    private String failureCode;
    private String failureComments;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt;

    public SmsRequestIndex() {
    }

    public SmsRequestIndex(String id, String phoneNumber, String message, String status, String failureCode, String failureComments, Date createdAt, Date updatedAt) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.status = status;
        this.failureCode = failureCode;
        this.failureComments = failureComments;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFailureCode() {
        return failureCode;
    }

    public void setFailureCode(String failureCode) {
        this.failureCode = failureCode;
    }

    public String getFailureComments() {
        return failureComments;
    }

    public void setFailureComments(String failureComments) {
        this.failureComments = failureComments;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
