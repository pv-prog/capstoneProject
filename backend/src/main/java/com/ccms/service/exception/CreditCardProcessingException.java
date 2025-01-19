package com.ccms.service.exception;

/**
 * Custom exception to indicate errors occurring during credit card processing.
 * This exception extends RuntimeException and can be thrown when an error happens
 * while processing a credit card transaction or any related operation.
 */

public class CreditCardProcessingException extends RuntimeException {
	
    /**
     * Constructor to create a new CreditCardProcessingException with a specific message 
     * and the underlying cause.
     *
     * @param message A detailed message describing the exception.
     * @param cause The cause of the exception (usually another throwable).
     */
	
    public CreditCardProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}