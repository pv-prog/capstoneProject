package com.ccms.service.exception;

public class CreditCardProcessingException extends RuntimeException {
	
    public CreditCardProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}