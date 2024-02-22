package com.assignment.notificationservice.entity.imiconnect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Msisdn {
    private List<String> msisdn;
    private Integer correlationId;

    public String toString() {
        return String.format("msisdn : %s, correlationId : {}", msisdn.get(0), correlationId);
    }
}
