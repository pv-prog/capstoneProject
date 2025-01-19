package com.ccms.service.utilities;

import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ccms.service.exception.InvalidUsernameException;

/**
 * Utility class to decode Base64-encoded usernames.
 * <p>
 * This class provides a method to decode a Base64-encoded username. It performs several validation checks 
 * before decoding, including verifying that the username is not null or empty, does not exceed the maximum 
 * allowed length, and ensures that the Base64 string is correctly padded. If any of the validation checks fail, 
 * an appropriate exception is thrown. If the Base64 decoding fails, it also logs the error and throws a custom 
 * exception.
 * </p>
 * 
 * @see InvalidUsernameException
 */

@Component
public class Decodename {

	private static final int MAX_USERNAME_LENGTH = 25;
	private static final Logger logger = LoggerFactory.getLogger(Decodename.class);

	public String decodeUsername(String encodedUsername) {
		
		// Check if encoded username is null or empty
		
		if (encodedUsername == null || encodedUsername.trim().isEmpty()) {
			logger.error("Username is null or empty.");
			throw new InvalidUsernameException("Username cannot be null or empty");
		}

		// Check if encoded username exceeds the maximum allowed length
		
		if (encodedUsername.length() > MAX_USERNAME_LENGTH) {
			logger.error("Username exceeds maximum length of {}", MAX_USERNAME_LENGTH);
			throw new InvalidUsernameException("Username must not exceed " + MAX_USERNAME_LENGTH + " characters");
		}

		// Ensure that the Base64 encoded username is valid
		
		String paddedEncodedUsername = ensureBase64Padding(encodedUsername);

		try {
			// Decode the Base64 username
			return new String(Base64.getDecoder().decode(paddedEncodedUsername));
		} catch (IllegalArgumentException e) {
			logger.error("Error decoding Base64 username", e);
			throw new InvalidUsernameException("Invalid Base64 encoding for username");
		}
	}

	// Ensure Base64 padding is valid for decoding
	
	private String ensureBase64Padding(String encodedUsername) {
		int paddingLength = encodedUsername.length() % 4;
		if (paddingLength == 2) {
			return encodedUsername + "==";
		} else if (paddingLength == 3) {
			return encodedUsername + "=";
		}
		return encodedUsername; // Already properly padded
	}
}
