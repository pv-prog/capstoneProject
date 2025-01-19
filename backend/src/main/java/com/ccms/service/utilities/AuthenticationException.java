package com.ccms.service.utilities;

/**
 * Custom exception class to represent authentication-related errors in the
 * application.
 * <p>
 * This exception extends {@link RuntimeException} and is used to signal
 * authentication failures or issues during the authentication process. It can
 * be thrown when invalid credentials are provided, or other
 * authentication-related errors occur.
 * </p>
 * 
 * @see RuntimeException
 */

public class AuthenticationException extends RuntimeException {

	// Default constructor
	public AuthenticationException(String message) {
		super(message);
	}

	// Constructor that accepts a message and a cause
	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
}
