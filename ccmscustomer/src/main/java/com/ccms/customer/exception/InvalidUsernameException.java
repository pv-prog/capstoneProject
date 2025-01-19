package com.ccms.customer.exception;

/**
 * Custom exception to handle invalid username errors.
 * <p>
 * This exception is thrown when the username provided by the user is in an invalid format or does not meet
 * the required encoding or structure. It extends {@link RuntimeException} and can be used for indicating
 * issues related to invalid usernames during the authentication or registration process.
 */

public class InvalidUsernameException extends RuntimeException {
    public InvalidUsernameException(String message) {
        super(message);
    }
}
