package com.assignment.notificationservice.dto.responsedto;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BlacklistedNumbersResponseDTO {
    List<String> data;

    public BlacklistedNumbersResponseDTO() {
    }

    public BlacklistedNumbersResponseDTO(List<String> data) {
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
