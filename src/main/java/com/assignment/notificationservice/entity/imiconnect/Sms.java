package com.assignment.notificationservice.entity.imiconnect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Sms {
    private String text;
    public String toString(){
        return String.format("text : %s",text);
    }
}
