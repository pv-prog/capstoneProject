package com.ccms.service.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String username) {
        super("User not found: " + username);
    }
}