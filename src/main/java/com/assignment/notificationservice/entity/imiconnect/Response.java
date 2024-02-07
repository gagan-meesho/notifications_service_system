package com.assignment.notificationservice.entity.imiconnect;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    int code;
    UUID transid;
    String description;
    int correlationid;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public UUID getTransid() {
        return transid;
    }

    public Response() {
    }

    public void setTransid(UUID transid) {
        this.transid = transid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCorrelationid() {
        return correlationid;
    }

    public void setCorrelationid(int correlationid) {
        this.correlationid = correlationid;
    }

    public Response(int code, UUID transid, String description, int correlationid) {
        this.code = code;
        this.transid = transid;
        this.description = description;
        this.correlationid = correlationid;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", transid=" + transid +
                ", description='" + description + '\'' +
                ", correlationid=" + correlationid +
                '}';
    }
}
