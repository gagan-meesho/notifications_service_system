package com.assignment.notificationservice.dto.requestbodydto;

import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ToString
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
