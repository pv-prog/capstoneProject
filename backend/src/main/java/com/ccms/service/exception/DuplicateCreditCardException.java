package com.ccms.service.exception;

/**
 * Custom exception for handling duplicate credit card entries. This exception
 * extends RuntimeException and is used to indicate that a credit card already
 * exists in the system.
 */

public class DuplicateCreditCardException extends RuntimeException {

	/**
	 * Constructor to create a new DuplicateCreditCardException with a specific
	 * message.
	 *
	 * @param message The detailed message about the duplicate credit card.
	 */

	public DuplicateCreditCardException(String message) {
		super(message);
	}
}