package com.assignment.notificationservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

@ControllerAdvice
@Slf4j
public class GlobalExceptions {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        return ResponseEntity.badRequest().body("Bad request. Please enter valid values.");
    }
    @ExceptionHandler(NotificationServiceException.class)
    public ResponseEntity<String> handleNotificationServiceException(NotificationServiceException ex){
        return ResponseEntity.internalServerError().body("Notification service exception.");
    }
    @ExceptionHandler(BadRequestErrorException.class)
    public ResponseEntity<String> handleBadRequestErrorException(BadRequestErrorException ex){
        return ResponseEntity.badRequest().body(ex.getMessage().isEmpty()?"Received bad request from client.":ex.getMessage());
    }
     @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleCustomException(CustomException ex){
        return ResponseEntity.status(900).body(ex.getMessage());
    }
    @ExceptionHandler(CustomIOException.class)
    public ResponseEntity<String> handleCustomIOException(CustomIOException ex){
        return ResponseEntity.status(87).body(ex.getMessage());
    }
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<String> handleInternalServerErrorException(InternalServerErrorException ex){
        return ResponseEntity.status(500).body("Internal server error. please try later.");
    }
    @ExceptionHandler(RedisInteralServerErrorException.class)
    public ResponseEntity<String> handleRedisInternalServerErrorException(RedisInteralServerErrorException ex){
        return ResponseEntity.status(500).body("Internal server exception in redis.");
    }
    @ExceptionHandler(SqlInternalServerErrorException.class)
    public ResponseEntity<String> handleSqlInternalServerErrorException(SqlInternalServerErrorException ex){
        return ResponseEntity.status(500).body("Internal server exception in jdbc server.");
    }
    @ExceptionHandler(InterruptedException.class)
    public ResponseEntity<String> handleApiTimeoutException(InterruptedException ex){
        log.error("api timed out.");
        return ResponseEntity.status(500).body("Api request timed out.");
    }
    @ExceptionHandler(Exception.class)
    public void handleException(Exception ex){
        log.error("Some Exceptio occured.try again later");
    }
    @ExceptionHandler(ListenerExecutionFailedException.class)
    public ResponseEntity<String> handleException(ListenerExecutionFailedException ex){
        log.error("kafka listener failed");
        return ResponseEntity.status(400).body("Kafka listener failed.");
    }
    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<String> handleException(KafkaException ex){
        log.error("kafka exception ");
        return ResponseEntity.status(400).body("Kafka exception.");
    }
}
