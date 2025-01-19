package com.ccms.service.utilities;

import org.springframework.stereotype.Component;

/**
 * A utility class to format and unformat credit card numbers.
 * <p>
 * This class provides methods to mask a credit card number for security purposes and to unmask
 * it when the full number is needed. The masking method hides the first 12 digits of the card number
 * and shows only the last 4 digits. The unmasking method restores the credit card number to its original
 * form by adding dashes between every four digits.
 * </p>
 * 
 * <p>
 * Note: The class assumes that credit card numbers are 16 digits long. If an invalid length is detected,
 * an {@link IllegalArgumentException} will be thrown.
 * </p>
 */

@Component
public class CreditCardFormatter {

	// Method to mask the credit card number (show only last 4 digits)
	public String maskCreditCardNumber(String creditCardNumber) {
		// Remove all non-numeric characters (e.g., spaces or dashes)
		String numericCardNumber = creditCardNumber.replaceAll("[^0-9]", "");

		// Check for valid card length (16 digits)
		if (numericCardNumber.length() != 16) {
			throw new IllegalArgumentException("Invalid credit card number length.");
		}

		// Mask the first 12 digits with asterisks and keep the last 4 digits visible
		return "****-****-****-" + numericCardNumber.substring(12);
	}

	// Method to unmask the credit card number (return the full number)
	public String unmaskCreditCardNumber(String maskedCardNumber) {
		// Remove all non-numeric characters (e.g., spaces or dashes)
		
		String numericCardNumber = maskedCardNumber.replaceAll("[^0-9]", "");
		
		// Check that the number is 16 digits
		if (numericCardNumber.length() != 16) {
			throw new IllegalArgumentException("Invalid masked card number length.");
		}
		
	    String unmaskedCardNumber = numericCardNumber.replaceAll("(\\d{4})(\\d{4})(\\d{4})(\\d{4})", "$1-$2-$3-$4");

		// Return the full unmasked card number
		return unmaskedCardNumber;
	}

}
