package com.assignment.notificationservice.exception;


import lombok.*;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ApiTimeoutException extends AsyncRequestTimeoutException {
    String message;
}
