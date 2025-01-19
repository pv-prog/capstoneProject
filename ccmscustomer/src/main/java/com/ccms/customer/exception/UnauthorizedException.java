package com.ccms.customer.exception;

/**
 * Custom exception to handle unauthorized access errors.
 * <p>
 * This exception is thrown when a user attempts to access a resource or perform an action
 * they do not have permission to do. It extends {@link RuntimeException} and is used 
 * to indicate that the user is not authorized to perform the requested operation.
 */

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
