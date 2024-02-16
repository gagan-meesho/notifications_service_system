package com.assignment.notificationservice.dto.requestDTO.sql;

import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Component;

@Component
public class SendSmsResponseDTO {

    @Size(min = 7, max = 10, message = "Please enter a valid phone number.")
    private String phoneNumber;
    @Size(min = 1, message = "message cannot be empty.")
    private String message;

    public SendSmsResponseDTO() {
    }

    public SendSmsResponseDTO(String phoneNumber, String message) {
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
