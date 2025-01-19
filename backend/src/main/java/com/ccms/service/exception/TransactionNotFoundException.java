package com.ccms.service.exception;

/**
 * Custom exception class to handle scenarios where no credit card transactions
 * are found for a specific user.
 * <p>
 * This exception is thrown when the system cannot find any transaction records
 * associated with a particular username, which could indicate that either the
 * user has no transaction history or that there is an issue with the data
 * retrieval process.
 * </p>
 */

public class TransactionNotFoundException extends RuntimeException {

	/**
	 * Constructs a new TransactionNotFoundException with a detail message. The
	 * detail message is saved for later retrieval by the
	 * {@link Throwable#getMessage()} method.
	 *
	 * @param username The username for which no credit card transactions were
	 *                 found. This is used to provide a more specific error message.
	 */

	public TransactionNotFoundException(String username) {
		super("No credit card transactions found for username: " + username);
	}
}