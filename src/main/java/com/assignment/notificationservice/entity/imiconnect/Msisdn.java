package com.assignment.notificationservice.entity.imiconnect;

import lombok.*;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Msisdn {
    private List<String> msisdn;
    private Integer correlationId;
}
