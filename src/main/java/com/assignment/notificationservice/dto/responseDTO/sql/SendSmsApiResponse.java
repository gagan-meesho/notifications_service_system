package com.assignment.notificationservice.dto.responseDTO.sql;

public class SendSmsApiResponse {
    protected String phoneNumber;

    protected String message;

    public SendSmsApiResponse() {
    }

    public SendSmsApiResponse(String phoneNumber, String message) {
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
