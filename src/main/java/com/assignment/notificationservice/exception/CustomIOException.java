package com.assignment.notificationservice.exception;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CustomIOException extends NotificationServiceException{
String message;
}
