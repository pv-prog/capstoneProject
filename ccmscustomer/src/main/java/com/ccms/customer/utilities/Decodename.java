package com.ccms.customer.utilities;

import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ccms.customer.exception.InvalidUsernameException;

/**
 * A service class that decodes an encoded username, ensuring it meets certain validation criteria.
 * <p>
 * This class includes validation for:
 * <ul>
 *   <li>Null or empty username</li>
 *   <li>Username length exceeding the maximum allowed length (25 characters)</li>
 *   <li>Valid Base64 encoding</li>
 * </ul>
 * </p>
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
