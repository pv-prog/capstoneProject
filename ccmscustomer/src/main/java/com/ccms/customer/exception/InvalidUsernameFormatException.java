package com.ccms.customer.exception;

/**
 * Custom exception to handle errors related to invalid username format.
 * <p>
 * This exception is thrown when a username fails to adhere to the required format.
 * It extends {@link RuntimeException} and can be used to indicate issues during 
 * username validation, such as incorrect Base64 encoding or invalid length.
 */

public class InvalidUsernameFormatException extends RuntimeException {
    public InvalidUsernameFormatException(String message) {
        super(message);
    }
}
