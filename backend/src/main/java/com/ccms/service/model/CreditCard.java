package com.ccms.service.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Represents a CreditCard entity.
 * <p>
 * This class holds information about a credit card for a specific user. It
 * contains the user-related details (like username and name on the card), and a
 * list of credit card details (such as credit card number, expiry date, CVV,
 * etc.).
 * </p>
 * 
 * The document is stored in the MongoDB collection "CreditCard".
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "CreditCard")
public class CreditCard {

	@Id
	private String id;
	private String username;
	private String nameOnTheCard;
	private List<CreditCardDetail> creditcards;

	/**
	 * Inner class representing the detailed information of a specific credit card.
	 * <p>
	 * This contains attributes like the credit card number, expiry date, CVV,
	 * transaction vendor, and the status of the credit card.
	 * </p>
	 */

	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	public static class CreditCardDetail {

		// @JsonIgnore
		@Schema(hidden = true)
		private int creditCardId;
		private String creditCardNumber;
		private int expiryMonth;
		private int expiryYear;
		private int cvv;
		private String wireTransactionVendor;
		private String status;
	}

}
