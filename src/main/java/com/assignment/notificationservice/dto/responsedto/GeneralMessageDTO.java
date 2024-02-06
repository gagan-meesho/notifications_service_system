package com.assignment.notificationservice.dto.responsedto;

public class GeneralMessageDTO {
    private String data;

    public GeneralMessageDTO() {
    }

    public GeneralMessageDTO(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
