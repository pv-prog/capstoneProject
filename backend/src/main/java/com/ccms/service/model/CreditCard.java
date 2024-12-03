package com.ccms.service.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	public static class CreditCardDetail {

		private int creditCardId;
		private String creditCardNumber;
		private int expiryMonth;
		private int expiryYear;
		private int cvv;
		private String wireTransactionVendor;
		private String status;
	}

}
