package com.assignment.notificationservice.dto.responseDTO.sql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmsSendSuccess extends SendSmsApiResponse{
    private String phoneNumber;
    private String message;
}
