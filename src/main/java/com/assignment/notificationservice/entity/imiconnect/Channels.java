package com.assignment.notificationservice.entity.imiconnect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
