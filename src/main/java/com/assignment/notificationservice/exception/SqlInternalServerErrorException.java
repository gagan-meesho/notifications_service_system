package com.assignment.notificationservice.exception;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlInternalServerErrorException extends InternalServerErrorException {
    String message;
}
