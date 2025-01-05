package com.ccms.service.exception;

public class InvalidUsernameFormatException extends RuntimeException {
    public InvalidUsernameFormatException(String message) {
        super(message);
    }
}
