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

@Document(collection = "Transactions")
public class Transaction {

	@Id
	private String id;
	private String username;
	private List<CreditCardTransaction> creditcards;

	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	public static class CreditCardTransaction {

		private int creditCardId;
		private List<TransactionDetail> transactions;

	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	public static class TransactionDetail {

		private long transactionId;
		private String transactionDate;
		private String transactionTime;
		private String transactionType; // cr or db
		private double transactionAmount;

	}

}
