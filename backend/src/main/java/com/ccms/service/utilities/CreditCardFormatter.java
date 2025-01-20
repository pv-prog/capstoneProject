package com.ccms.service.utilities;

import org.springframework.stereotype.Component;

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
