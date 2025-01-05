package com.ccms.service.exception;

public class DuplicateCreditCardException extends RuntimeException {
    public DuplicateCreditCardException(String message) {
        super(message);
    }
}