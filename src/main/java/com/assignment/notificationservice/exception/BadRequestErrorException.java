package com.assignment.notificationservice.exception;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class BadRequestErrorException extends NotificationServiceException{
    String message;
}
