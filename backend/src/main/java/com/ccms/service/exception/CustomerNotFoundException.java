package com.ccms.service.exception;

/**
 * Custom exception to indicate that the customer with the specified username
 * was not found. This exception extends RuntimeException and can be used to
 * signal the absence of a customer in database queries or service operations.
 */

public class CustomerNotFoundException extends RuntimeException {

	/**
	 * Constructor to create a new CustomerNotFoundException with a specific
	 * message.
	 *
	 * @param username The username of the customer that was not found.
	 */

	public CustomerNotFoundException(String username) {
		super("User not found: " + username);
	}
}