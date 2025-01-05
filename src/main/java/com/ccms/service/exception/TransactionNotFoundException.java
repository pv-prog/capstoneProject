package com.ccms.service.exception;

public class TransactionNotFoundException extends RuntimeException{
    public TransactionNotFoundException(String username) {
        super("No credit card transactions found for username: " + username);
    }
}