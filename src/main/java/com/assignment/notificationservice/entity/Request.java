package com.assignment.notificationservice.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name="sms_requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    @Column(name="phone_number")
    private String phoneNumber;
    @Column(name="message")
    private String message;
    @Column(name="status")
    private String status;
    @Column(name="failure_code")
    private Integer failureCode;
    @Column(name="failure_comments")
    private String failureComments;
    @Column(name="created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdAt;

    @Column(name="updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp updatedAt;

    public Request() {
    }

    public Request(String phoneNumber, String message, String status, Integer failureCode, String failureComments) {
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.status = status;
        this.failureCode = failureCode;
        this.failureComments = failureComments;
        this.createdAt=Timestamp.valueOf(LocalDateTime.now());
        this.updatedAt=this.createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Integer getFailureCode() {
        return failureCode;
    }

    public void setFailureCode(Integer failureCode) {
        this.failureCode = failureCode;
    }

    public String getFailureComments() {
        return failureComments;
    }

    public void setFailureComments(String failureComments) {
        this.failureComments = failureComments;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
