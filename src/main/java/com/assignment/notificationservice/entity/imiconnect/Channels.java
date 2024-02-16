package com.assignment.notificationservice.entity.imiconnect;

import lombok.*;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Channels {
    private Sms sms;

    public String toString() {
        return String.format(" sms : %s", sms.toString());
    }
}
