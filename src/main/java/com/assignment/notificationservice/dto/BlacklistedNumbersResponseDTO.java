package com.assignment.notificationservice.dto;

import java.util.List;

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
