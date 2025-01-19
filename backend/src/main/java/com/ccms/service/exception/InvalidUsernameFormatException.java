package com.ccms.service.exception;

/**
 * Custom exception class to handle invalid username format scenarios.
 * <p>
 * This exception is thrown when the provided username does not match the
 * expected format, such as when the username contains illegal characters or
 * violates the required structure or pattern.
 * </p>
 */

public class InvalidUsernameFormatException extends RuntimeException {

	/**
	 * Constructs a new InvalidUsernameFormatException with the specified detail
	 * message. The detail message is saved for later retrieval by the
	 * {@link Throwable#getMessage()} method.
	 *
	 * @param message The detail message explaining the reason for the exception. It
	 *                should describe the issue with the username format.
	 */

	public InvalidUsernameFormatException(String message) {
		super(message);
	}
}
