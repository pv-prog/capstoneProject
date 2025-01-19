package com.ccms.service.exception;

/**
 * Custom exception class to handle invalid username scenarios.
 * <p>
 * This exception is thrown when the provided username is invalid or cannot be
 * processed due to formatting issues, encoding problems, or other validation
 * failures.
 * </p>
 */

public class InvalidUsernameException extends RuntimeException {

	/**
	 * Constructs a new InvalidUsernameException with the specified detail message.
	 * The detail message is saved for later retrieval by the
	 * {@link Throwable#getMessage()} method.
	 * 
	 * @param message The detail message that explains the reason for the exception.
	 */

	public InvalidUsernameException(String message) {
		super(message);
	}
}
