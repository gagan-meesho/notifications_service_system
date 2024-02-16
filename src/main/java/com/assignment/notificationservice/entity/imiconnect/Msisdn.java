package com.assignment.notificationservice.entity.imiconnect;

import lombok.*;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

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
