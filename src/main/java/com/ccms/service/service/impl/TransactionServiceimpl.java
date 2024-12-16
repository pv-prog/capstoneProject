package com.ccms.service.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ccms.service.model.Transaction;
import com.ccms.service.exception.UserNotFoundException;
import com.ccms.service.model.CreditCard.CreditCardDetail;
import com.ccms.service.model.Transaction.CreditCardTransaction;
import com.ccms.service.model.Transaction.TransactionDetail;
import com.ccms.service.repository.TransactionRepository;
import com.ccms.service.service.CreditCardService;
import com.ccms.service.model.TransactionWithCreditCardInfo;
import com.ccms.service.service.TransactionService;
import com.ccms.service.utilities.CreditCardEnDecryption;
import com.ccms.service.utilities.CreditCardFormatter;

@Service
public class TransactionServiceimpl implements TransactionService {

	@Autowired
	private CreditCardService cardService; // To fetch credit cards

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	CreditCardEnDecryption cardEnDecryption;

	@Autowired
	CreditCardFormatter cardFormatter;

	// Get User Transactions
	@Override
	public Transaction getTransactionsforuser(String username) {

		return transactionRepository.findByUsername(username);

	}

	// Customer wants to view last month maximum expenses of all cards
	@Override
	public List<Map<String, Object>> getMaxExpensesForLastMonth(String username, String statusFilter) {

		// Retrieve all active credit cards for the user
		System.out.println("Entering getMaxExpensesForLastMonth");

		List<CreditCardDetail> activecreditcards = new ArrayList<>();

		try {
			// Attempt to retrieve the user's active credit cards
			activecreditcards = cardService.getallCreditcardsforuser(username).getCreditcards();

			// Check if the user was found
			if (activecreditcards == null || activecreditcards.isEmpty()) {
				throw new UserNotFoundException(username); // Throw exception if no active credit cards are found
			}

		} catch (UserNotFoundException e) {

			// Handle user not found exception separately, if needed
			System.err.println(e.getMessage());
			return Collections.emptyList(); // Optionally, return an empty list or handle differently
		} catch (Exception e) {

			// Handle other exceptions, such as database connection issues
			System.err.println("Error retrieving credit cards for user: " + username);
			e.printStackTrace();
			return Collections.emptyList(); // Optionally, return an empty list in case of an error
		}

		List<CreditCardDetail> filteredCreditCards = activecreditcards.stream().filter(card -> {
			switch (statusFilter.toLowerCase()) {
			case "enabled":
				return card.getStatus().equalsIgnoreCase("enabled");
			case "disabled":
				return card.getStatus().equalsIgnoreCase("disabled");
			case "both":
				return true; // Include all cards
			default:
				throw new IllegalArgumentException("Invalid status filter: " + statusFilter);
			}
		}).collect(Collectors.toList());

		List<CreditCardTransaction> cardTransactions = new ArrayList<>();

		try {
			// Fetch the credit card transactions for the given user
			cardTransactions = transactionRepository.findByUsername(username).getCreditcards().stream()
					.collect(Collectors.toList());

			// Check if creditCard is null or if the credit cards list is null
			if (cardTransactions == null || activecreditcards.isEmpty()) {
				throw new UserNotFoundException(username);

			}
		} catch (UserNotFoundException e) {
			System.err.println(e.getMessage());
			return Collections.emptyList();
		}

		catch (Exception e) {

			// Handle other exceptions, such as database connection issues
			System.err.println("Error retrieving Transactions for user: " + username);
			e.printStackTrace();
			return Collections.emptyList(); // Optionally, return an empty list in case of an error
		}

		// Initialize a map to hold the maximum transaction amount for each card
		Map<Integer, List<TransactionWithCreditCardInfo>> allTransactionDetails = new HashMap<>();

		// Retrieve transactions for each active credit card

		for (CreditCardDetail creditCardDetail : filteredCreditCards) {

			List<TransactionWithCreditCardInfo> filteredTransactions = cardTransactions.stream()
					.filter(transaction -> transaction.getCreditCardId() == creditCardDetail.getCreditCardId())
					.flatMap(transaction -> transaction.getTransactions().stream())
					.map(transaction -> new TransactionWithCreditCardInfo(creditCardDetail.getCreditCardNumber(),
							transaction))
					.collect(Collectors.toList());

			if (!filteredTransactions.isEmpty()) {
				allTransactionDetails.put(creditCardDetail.getCreditCardId(), filteredTransactions);
			}
		}

		// List to store the results as specified in the required format
		List<Map<String, Object>> result = new ArrayList<>();

		// Calculate the date range for the last month
		LocalDate now = LocalDate.now();
		LocalDate startOfLastMonth = now.minusMonths(1).withDayOfMonth(1);
		LocalDate endOfLastMonth = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());

		System.out.println("Date range: " + startOfLastMonth + " to " + endOfLastMonth);

		// DateTimeFormatter to extract month name
		DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM");

		// Iterate through each card's transactions to find the max transaction amount
		for (Map.Entry<Integer, List<TransactionWithCreditCardInfo>> entry : allTransactionDetails.entrySet()) {

			List<TransactionWithCreditCardInfo> transactions = entry.getValue();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

			// Filter transactions within the date range
			List<TransactionWithCreditCardInfo> maxExpenseTransactions = transactions.stream().filter(t -> {
				LocalDate transactionDate = LocalDate.parse(t.getTransactionDetail().getTransactionDate(), formatter);
				return (transactionDate.isEqual(startOfLastMonth) || transactionDate.isAfter(startOfLastMonth))
						&& (transactionDate.isEqual(endOfLastMonth) || transactionDate.isBefore(endOfLastMonth));
			}).collect(Collectors.toList());

			// Calculate the maximum transaction amount for the last month
			if (!maxExpenseTransactions.isEmpty()) {
				double maxTransactionAmount = maxExpenseTransactions.stream()
						.mapToDouble(t -> t.getTransactionDetail().getTransactionAmount()).max().orElse(0.0);

				String month = startOfLastMonth.format(monthFormatter).toUpperCase();

				Map<String, Object> expenseDetails = new LinkedHashMap<>();

				try {

					String decryptedCreditCardNumber = null;

					try {
						decryptedCreditCardNumber = cardEnDecryption
								.decrypt(entry.getValue().get(0).getCreditCardNumber());
					} catch (Exception e) {

						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// Set the decrypted credit card number back into the CreditCard object

					entry.getValue().get(0).setCreditCardNumber(decryptedCreditCardNumber);

					String formattedCreditCardNumber = cardFormatter
							.maskCreditCardNumber(entry.getValue().get(0).getCreditCardNumber());

					expenseDetails.put("credit_card", formattedCreditCardNumber);
					expenseDetails.put("month", month);
					expenseDetails.put("amount", maxTransactionAmount);

					result.add(expenseDetails);

				} catch (Exception e) {

					// Log and handle decryption error
					throw new RuntimeException("Error decrypting credit card number for user: " + username, e);
				}

			}
		}

		return result;
	}

	// HIGH Value expenses more than the threshold amount (xxxxx)
	@Override
	public Map<String, List<Map<String, String>>> getHighValueExpensesForUser(String username, int limit,
			String statusFilter, double amountThreshold) {

		// Retrieve all active credit cards for the user
		System.out.println("Entering getHighValueExpensesForUser");

		List<CreditCardDetail> activecreditcards = new ArrayList<>();

		try {
			// Attempt to retrieve the user's active credit cards
			activecreditcards = cardService.getallCreditcardsforuser(username).getCreditcards();

			// Check if the user was found
			if (activecreditcards == null || activecreditcards.isEmpty()) {
				throw new UserNotFoundException(username); // Throw exception if no active credit cards are found
			}

		} catch (UserNotFoundException e) {

			// Handle user not found exception separately, if needed
			System.err.println(e.getMessage());
			return Collections.emptyMap();
		} catch (Exception e) {

			// Handle other exceptions, such as database connection issues
			System.err.println("Error retrieving credit cards for user: " + username);
			e.printStackTrace();
			return Collections.emptyMap();
		}

		List<CreditCardDetail> filteredCreditCards = activecreditcards.stream().filter(card -> {
			switch (statusFilter.toLowerCase()) {
			case "enabled":
				return card.getStatus().equalsIgnoreCase("enabled");
			case "disabled":
				return card.getStatus().equalsIgnoreCase("disabled");
			case "both":
				return true;
			default:
				throw new IllegalArgumentException("Invalid status filter: " + statusFilter);
			}
		}).collect(Collectors.toList());

		// Fetch all transactions for the user in a single query

		List<CreditCardTransaction> cardTransactions = new ArrayList<>();

		try {
			// Fetch the credit card transactions for the given user
			cardTransactions = transactionRepository.findByUsername(username).getCreditcards().stream()
					.collect(Collectors.toList());

			// Check if creditCard is null or if the credit cards list is null
			if (cardTransactions == null || activecreditcards.isEmpty()) {
				throw new UserNotFoundException(username);

			}
		} catch (UserNotFoundException e) {
			System.err.println(e.getMessage());
			return Collections.emptyMap();
		}

		catch (Exception e) {

			// Handle other exceptions, such as database connection issues
			System.err.println("Error retrieving Transactions for user: " + username);
			e.printStackTrace();
			return Collections.emptyMap();
		}

		// Retrieve transactions for each active credit card

		Map<String, List<Map<String, String>>> allTransactionDetails = new LinkedHashMap();

		for (CreditCardDetail creditCardDetail : filteredCreditCards) {

			// Filter the transactions for this particular credit card

			List<Map<String, String>> filteredTransactions = cardTransactions.stream()
					.filter(transaction -> transaction.getCreditCardId() == creditCardDetail.getCreditCardId())
					.flatMap(transaction -> transaction.getTransactions().stream())
					.filter(transaction -> transaction.getTransactionAmount() > amountThreshold).map(
							transaction -> new TransactionWithCreditCardInfo(creditCardDetail.getCreditCardNumber(),
									transaction))
					.sorted(Comparator.comparing((TransactionWithCreditCardInfo transaction) -> transaction
							.getTransactionDetail().getTransactionDate()).reversed())
					.map(transaction -> {
						// Create a map with "transactionDetail" key and the transaction detail data as
						// strings
						Map<String, String> transactionMap = new LinkedHashMap<>();
						TransactionDetail detail = transaction.getTransactionDetail();
						transactionMap.put("transactionId", String.valueOf(detail.getTransactionId()));
						transactionMap.put("transactiondate", String.valueOf(detail.getTransactionDate()));
						transactionMap.put("transactiontime", String.valueOf(detail.getTransactionTime()));
						transactionMap.put("transactiontype", detail.getTransactionType());
						transactionMap.put("transactionamount", String.valueOf(detail.getTransactionAmount()));
						transactionMap.put("transactiondescription", detail.getTransactionDesc());
						return transactionMap;
					}).limit(limit).collect(Collectors.toList());

			if (!filteredTransactions.isEmpty()) {

				try {

					String decryptedCreditCardNumber = null;

					try {
						decryptedCreditCardNumber = cardEnDecryption.decrypt(creditCardDetail.getCreditCardNumber());
					} catch (Exception e) {

						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					String formattedCreditCardNumber = cardFormatter.maskCreditCardNumber(decryptedCreditCardNumber);

					allTransactionDetails.put(formattedCreditCardNumber, filteredTransactions);

				} catch (Exception e) {

					// Log and handle decryption error

					throw new RuntimeException("Error decrypting credit card number for user: " + username, e);
				}

			}
		}

		if (allTransactionDetails.isEmpty()) {
			System.out.println("No high-value transactions found for any card.");
		}

		return allTransactionDetails;

	}

	// Customer wants to view last x number of expenses of all cards
	@Override
	public Map<Integer, List<TransactionDetail>> getLastXTransactionsForUser(String username, int limit,
			String statusFilter) {

		// Retrieve all active credit cards for the user

		System.out.println("Entering getLastXTransactionsForUser");

		List<CreditCardDetail> activecreditcards = new ArrayList<>();

		try {
			// Attempt to retrieve the user's active credit cards
			activecreditcards = cardService.getallCreditcardsforuser(username).getCreditcards();

			// Check if the user was found
			if (activecreditcards == null || activecreditcards.isEmpty()) {
				throw new UserNotFoundException(username); // Throw exception if no active credit cards are found
			}

		} catch (UserNotFoundException e) {

			// Handle user not found exception separately, if needed
			System.err.println(e.getMessage());
			return Collections.emptyMap();
		} catch (Exception e) {

			// Handle other exceptions, such as database connection issues
			System.err.println("Error retrieving credit cards for user: " + username);
			e.printStackTrace();
			return Collections.emptyMap();
		}

		List<CreditCardDetail> filteredCreditCards = activecreditcards.stream().filter(card -> {

			switch (statusFilter.toLowerCase()) {

			case "enabled":
				return card.getStatus().equalsIgnoreCase("enabled");
			case "disabled":
				return card.getStatus().equalsIgnoreCase("disabled");
			case "both":
				return true; // Include all cards
			default:
				throw new IllegalArgumentException("Invalid status filter: " + statusFilter);
			}
		}).collect(Collectors.toList());

		// Fetch all transactions for the user in a single query

		List<CreditCardTransaction> cardTransactions = new ArrayList<>();

		try {
			// Fetch the credit card transactions for the given user
			cardTransactions = transactionRepository.findByUsername(username).getCreditcards().stream()
					.collect(Collectors.toList());

			// Check if creditCard is null or if the credit cards list is null
			if (cardTransactions == null || activecreditcards.isEmpty()) {
				throw new UserNotFoundException(username);

			}
		} catch (UserNotFoundException e) {
			System.err.println(e.getMessage());
			return Collections.emptyMap();
		}

		catch (Exception e) {

			// Handle other exceptions, such as database connection issues
			System.err.println("Error retrieving Transactions for user: " + username);
			e.printStackTrace();
			return Collections.emptyMap();
		}

		// Retrieve transactions for each active credit card

		Map<Integer, List<TransactionDetail>> allTransactionDetails = new HashMap<>();

		for (CreditCardDetail creditCardDetail : filteredCreditCards) {

			List<TransactionDetail> filteredTransactions = cardTransactions.stream()
					.filter(transaction -> transaction.getCreditCardId() == creditCardDetail.getCreditCardId())
					.flatMap(transaction -> transaction.getTransactions().stream()).collect(Collectors.toList());

			if (!filteredTransactions.isEmpty()) {
				allTransactionDetails.put(creditCardDetail.getCreditCardId(), filteredTransactions);
			}

		}

		// Now limit the transactions to the last X for each credit card ID

		Map<Integer, List<TransactionDetail>> limitedTransactionDetails = new HashMap<>();

		for (Map.Entry<Integer, List<TransactionDetail>> entry : allTransactionDetails.entrySet()) {

			List<TransactionDetail> transactions = entry.getValue();

			// Sort and limit the transactions for the current credit card ID

			if (!transactions.isEmpty()) {

				List<TransactionDetail> lastXTransactions = transactions.stream()
						.sorted(Comparator.comparing(TransactionDetail::getTransactionDate).reversed()).limit(limit)
						.collect(Collectors.toList());

				// Add the limited list back to the new map

				limitedTransactionDetails.put(entry.getKey(), lastXTransactions);
			}

		}

		return limitedTransactionDetails; // Return the map with limited transactions

	}

	@Override
	public List<Map<String, Object>> getLastXExpensesForUser(String username, int limit, String statusFilter) {

		// Retrieve all active credit cards for the user
		System.out.println("Entering getLastXExpensesForUser");

		List<CreditCardDetail> activecreditcards = new ArrayList<>();

		try {
			// Attempt to retrieve the user's active credit cards
			activecreditcards = cardService.getallCreditcardsforuser(username).getCreditcards();

			// Check if the user was found
			if (activecreditcards == null || activecreditcards.isEmpty()) {
				throw new UserNotFoundException(username); // Throw exception if no active credit cards are found
			}

		} catch (UserNotFoundException e) {

			// Handle user not found exception separately, if needed
			System.err.println(e.getMessage());
			return Collections.emptyList(); // Optionally, return an empty list or handle differently
		} catch (Exception e) {

			// Handle other exceptions, such as database connection issues
			System.err.println("Error retrieving credit cards for user: " + username);
			e.printStackTrace();
			return Collections.emptyList(); // Optionally, return an empty list in case of an error
		}

		List<CreditCardDetail> filteredCreditCards = activecreditcards.stream().filter(card -> {
			switch (statusFilter.toLowerCase()) {
			case "enabled":
				return card.getStatus().equalsIgnoreCase("enabled");
			case "disabled":
				return card.getStatus().equalsIgnoreCase("disabled");
			case "both":
				return true; // Include all cards
			default:
				throw new IllegalArgumentException("Invalid status filter: " + statusFilter);
			}
		}).collect(Collectors.toList());

		// Fetch all transactions for the user in a single query

		List<CreditCardTransaction> cardTransactions = new ArrayList<>();

		try {
			// Fetch the credit card transactions for the given user
			cardTransactions = transactionRepository.findByUsername(username).getCreditcards().stream()
					.collect(Collectors.toList());

			// Check if creditCard is null or if the credit cards list is null
			if (cardTransactions == null || activecreditcards.isEmpty()) {
				throw new UserNotFoundException(username);

			}
		} catch (UserNotFoundException e) {
			System.err.println(e.getMessage());
			return Collections.emptyList();
		}

		catch (Exception e) {

			// Handle other exceptions, such as database connection issues
			System.err.println("Error retrieving Transactions for user: " + username);
			e.printStackTrace();
			return Collections.emptyList(); // Optionally, return an empty list in case of an error
		}

		List<Map<String, Object>> Maxexpense = new ArrayList<>();

		for (CreditCardDetail creditCardDetail : filteredCreditCards) {

			// Filter the transactions for this particular credit card
			List<TransactionWithCreditCardInfo> filteredTransactions = cardTransactions.stream()
					.filter(transaction -> transaction.getCreditCardId() == creditCardDetail.getCreditCardId())
					.flatMap(transaction -> transaction.getTransactions().stream())
					.sorted(Comparator.comparing(TransactionDetail::getTransactionDate).reversed()).limit(limit)
					.map(transaction -> new TransactionWithCreditCardInfo(creditCardDetail.getCreditCardNumber(),
							transaction))
					.collect(Collectors.toList());

			if (!filteredTransactions.isEmpty()) {

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

				List<Map<String, Object>> transactionList = filteredTransactions.stream().map(transaction -> {
					Map<String, Object> transactionMap = new LinkedHashMap<>();
					LocalDate transactionDate = LocalDate.parse(transaction.getTransactionDetail().getTransactionDate(),
							formatter);
					String month = transactionDate.getMonth().name().substring(0, 3);
					transactionMap.put("month", month);
					transactionMap.put("amount", transaction.getTransactionDetail().getTransactionAmount());
					transactionMap.put("description", transaction.getTransactionDetail().getTransactionDesc());
					return transactionMap;
				}).collect(Collectors.toList());

				Map<String, Object> creditCardData = new LinkedHashMap<>();

				try {

					String decryptedCreditCardNumber = null;

					try {
						decryptedCreditCardNumber = cardEnDecryption.decrypt(creditCardDetail.getCreditCardNumber());
					} catch (Exception e) {

						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					String formattedCreditCardNumber = cardFormatter.maskCreditCardNumber(decryptedCreditCardNumber);

					creditCardData.put("credit_card", formattedCreditCardNumber);
					creditCardData.put("transactions", transactionList);

					Maxexpense.add(creditCardData);

				} catch (Exception e) {

					// Log and handle decryption error
					throw new RuntimeException("Error decrypting credit card number for user: " + username, e);
				}
			}
		}
		return Maxexpense;
	}
}
