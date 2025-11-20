package com.multi.backend5_1_multi_fc.notification.exception;

public class NotificationRuntimeException extends RuntimeException {
    public NotificationRuntimeException(String message) {
        super(message);
    }
    public NotificationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}

class NotificationNotFoundException extends NotificationRuntimeException{
    public NotificationNotFoundException(Long id) {
        super(String.format("Notification with id %s not found", id));
    }
}