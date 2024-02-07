package com.assignment.notificationservice.entity.imiconnect;

import java.util.List;

public class FinalResponse {
    List<Response> response;

    public List<Response> getResponse() {
        return response;
    }

    public FinalResponse() {
    }

    public void setResponse(List<Response> response) {
        this.response = response;
    }

    public FinalResponse(List<Response> response) {
        this.response = response;
    }
}
