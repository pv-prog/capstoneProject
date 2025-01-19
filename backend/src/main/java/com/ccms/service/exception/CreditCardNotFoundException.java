package com.ccms.service.exception;

/**
 * Custom exception class to indicate that no credit card was found for a
 * specific username. This exception is a RuntimeException and is typically used
 * in the service layer when a credit card cannot be retrieved based on the
 * provided username.
 */

public class CreditCardNotFoundException extends RuntimeException {

	/**
	 * Constructor that accepts a username and constructs an error message
	 * indicating that no credit card was found for the specified username.
	 *
	 * @param username The username for which no credit card was found.
	 */

	public CreditCardNotFoundException(String username) {
		super("No credit card found for username: " + username);
	}

	/**
	 * Constructor that accepts a custom message to allow more flexibility in the
	 * error description.
	 * 
	 * @param message The custom error message.
	 */
	public CreditCardNotFoundException(String message, Throwable cause) {
		super(message, cause);

	}
}