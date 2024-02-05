package com.assignment.notificationservice.dto.requestbodydto;

import java.util.List;

public class BlacklistedNumbersDTO {
    List<String> phoneNumbers;

    public BlacklistedNumbersDTO() {
    }

    public BlacklistedNumbersDTO(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

}
