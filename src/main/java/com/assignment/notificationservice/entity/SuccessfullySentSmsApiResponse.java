package com.assignment.notificationservice.entity;

public class SuccessfullySentSmsApiResponse {
    private String phoneNumber;

    private String message;

    public SuccessfullySentSmsApiResponse() {
    }

    public SuccessfullySentSmsApiResponse(String phoneNumber, String message) {
        this.phoneNumber = phoneNumber;
        this.message = message;
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
}
