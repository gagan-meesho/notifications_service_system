package com.assignment.notificationservice.entity.imiconnect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Sms {
    private String text;

    public String toString() {
        return String.format("text : %s", text);
    }
}
