package com.assignment.notificationservice.exception;

public class NotificationServiceException extends RuntimeException{
    public NotificationServiceException(){
        super();
    }
    public NotificationServiceException(String message){
        super(message);
    }
    public NotificationServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationServiceException(Throwable cause) {
        super(cause);
    }

    protected NotificationServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
