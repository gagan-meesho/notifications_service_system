package com.assignment.notificationservice.exception;

import lombok.*;

public class RedisInteralServerErrorException extends InternalServerErrorException{
    public RedisInteralServerErrorException(String message){
        super(message);
    }
}
