package com.assignment.notificationservice.entity.imiconnect;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ImiConnectRequest {
    private String deliverychannel;
    private Channels channels;

    private List<Msisdn> destination;

    public String toString() {
        return String.format("deliverychannel : %s , channels : %s , destination : %s", deliverychannel, channels.toString(), destination.toString());
    }
}
