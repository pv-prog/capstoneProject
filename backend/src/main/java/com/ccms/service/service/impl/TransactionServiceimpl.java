package com.ccms.service.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ccms.service.model.Transaction;
import com.ccms.service.model.CreditCard.CreditCardDetail;
import com.ccms.service.model.Transaction.CreditCardTransaction;
import com.ccms.service.model.Transaction.TransactionDetail;
import com.ccms.service.repository.TransactionRepository;
import com.ccms.service.service.CreditCardService;
import com.ccms.service.model.TransactionWithCreditCardInfo;
import com.ccms.service.service.TransactionService;

@Service
public class TransactionServiceimpl implements TransactionService {

	@Autowired
	private CreditCardService cardService; // To fetch credit cards

	@Autowired
	TransactionRepository transactionRepository;

	// Get User Transactions
	@Override
	public Transaction getTransactionsforuser(String username) {

		System.out.println(transactionRepository.findByUsername(username));

		return transactionRepository.findByUsername(username);

	}

	// Customer wants to view last month maximum expenses of all cards
	@Override
	public List<Map<String, Object>> getMaxExpensesForLastMonth(String username, String statusFilter) {

		// Retrieve all active credit cards for the user
		System.out.println("Entering getMaxExpensesForLastMonth");

		List<CreditCardDetail> activecreditcards = cardService.getallCreditcardsforuser(username).getCreditcards();

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

		// Fetch all transactions of all current credit cards for the user
		List<CreditCardTransaction> cardTransactions = transactionRepository.findByUsername(username).getCreditcards()
				.stream().collect(Collectors.toList());

		// Initialize a map to hold the maximum transaction amount for each card
		Map<Integer, List<TransactionWithCreditCardInfo>> allTransactionDetails = new HashMap<>();

		// Retrieve transactions for each active credit card
		System.out.println("Retrieve transactions for each active credit card");

		for (CreditCardDetail creditCardDetail : filteredCreditCards) {

			System.out.println("Processing credit card: " + creditCardDetail);

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

				String formattedCreditCardNumber = formatCreditCardNumber(
						entry.getValue().get(0).getCreditCardNumber());

				expenseDetails.put("credit_card", formattedCreditCardNumber);
				expenseDetails.put("month", month);
				expenseDetails.put("amount", maxTransactionAmount);

				result.add(expenseDetails);
			}
		}

		System.out.println("Formatted result: " + result);

		return result;
	}

	private String formatCreditCardNumber(String creditCardNumber) {

		String numericCardNumber = creditCardNumber.replaceAll("[^0-9]", "");

		return numericCardNumber.replaceAll("(\\d{4})(\\d{4})(\\d{4})(\\d{4})", "$1-$2-$3-$4");
	}

	
	// Expenses more than the amount (xxxxx)
	@Override
	public Map<String, List<Map<String, TransactionDetail>>> getHighValueExpensesForUser(String username, int limit,
			String statusFilter, double amountThreshold) {

		// Retrieve all active credit cards for the user
		System.out.println("Entering getHighValueExpensesForUser");

		List<CreditCardDetail> activecreditcards = cardService.getallCreditcardsforuser(username).getCreditcards();

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

		List<CreditCardTransaction> cardTransactions = transactionRepository.findByUsername(username).getCreditcards()
				.stream().collect(Collectors.toList());

		// Retrieve transactions for each active credit card

		Map<String, List<Map<String, TransactionDetail>>> allTransactionDetails = new HashMap<>();

		System.out.println("Retrieve transactions for each active credit card");

		for (CreditCardDetail creditCardDetail : filteredCreditCards) {

			System.out.println("Processing credit card: " + creditCardDetail);

			// Filter the transactions for this particular credit card

			List<Map<String, TransactionDetail>> filteredTransactions = cardTransactions.stream()
					.filter(transaction -> transaction.getCreditCardId() == creditCardDetail.getCreditCardId())
					.flatMap(transaction -> transaction.getTransactions().stream())
					.filter(transaction -> transaction.getTransactionAmount() > amountThreshold).map(
							transaction -> new TransactionWithCreditCardInfo(creditCardDetail.getCreditCardNumber(),
									transaction))
					.sorted(Comparator.comparing((TransactionWithCreditCardInfo transaction) -> transaction
							.getTransactionDetail().getTransactionDate()).reversed())
					.map(transaction -> {
						// Create a map with "transactionDetail" key and the transaction detail object
						Map<String, TransactionDetail> transactionMap = new HashMap<>();
						transactionMap.put("transactionDetail", transaction.getTransactionDetail());
						return transactionMap;

					})
					.limit(limit)
					.collect(Collectors.toList());

			if (!filteredTransactions.isEmpty()) {
				allTransactionDetails.put(creditCardDetail.getCreditCardNumber(), filteredTransactions);
			}
		}

		if (allTransactionDetails.isEmpty()) {
			System.out.println("No high-value transactions found for any card.");
		}

		System.out.println("Filtered high-value transaction details: " + allTransactionDetails);

		return allTransactionDetails;

	}

	// Customer wants to view last x number of expenses of all cards
	@Override
	public Map<Integer, List<TransactionDetail>> getLastXTransactionsForUser(String username, int limit,
			String statusFilter) {

		// Retrieve all active credit cards for the user

		System.out.println("Entering getLastXTransactionsForUser");

		List<CreditCardDetail> activecreditcards = cardService.getallCreditcardsforuser(username).getCreditcards();

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

		List<CreditCardTransaction> cardTransactions = transactionRepository.findByUsername(username).getCreditcards()
				.stream().collect(Collectors.toList());

		// Retrieve transactions for each active credit card

		Map<Integer, List<TransactionDetail>> allTransactionDetails = new HashMap<>();

		System.out.println("Retrieve transactions for each active credit card");

		for (CreditCardDetail creditCardDetail : filteredCreditCards) {

			System.out.println("Processing credit card: " + creditCardDetail);

			List<TransactionDetail> filteredTransactions = cardTransactions.stream()
					.filter(transaction -> transaction.getCreditCardId() == creditCardDetail.getCreditCardId())
					.flatMap(transaction -> transaction.getTransactions().stream()).collect(Collectors.toList());

			if (!filteredTransactions.isEmpty()) {
				allTransactionDetails.put(creditCardDetail.getCreditCardId(), filteredTransactions);
			}

		}

		System.out.println("All transaction details: " + allTransactionDetails);

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
		System.out.println("Limited transaction details: " + limitedTransactionDetails);

		return limitedTransactionDetails; // Return the map with limited transactions

	}

	@Override
	public List<Map<String, Object>> getLastXExpensesForUser(String username, int limit, String statusFilter) {

		// Retrieve all active credit cards for the user
		System.out.println("Entering getLastXExpensesForUser");

		List<CreditCardDetail> activeCreditCards = cardService.getallCreditcardsforuser(username).getCreditcards();

		List<CreditCardDetail> filteredCreditCards = activeCreditCards.stream().filter(card -> {
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

		List<CreditCardTransaction> cardTransactions = transactionRepository.findByUsername(username).getCreditcards()
				.stream().collect(Collectors.toList());

		List<Map<String, Object>> Maxexpense = new ArrayList<>();

		System.out.println("Retrieve transactions for each active credit card");

		for (CreditCardDetail creditCardDetail : filteredCreditCards) {

			System.out.println("Processing credit card: " + creditCardDetail);

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

				Map<String, Object> creditCardData = new HashMap<>();

				creditCardData.put("credit_card", creditCardDetail.getCreditCardNumber());
				creditCardData.put("transactions", transactionList);

				Maxexpense.add(creditCardData);

			}
		}

		return Maxexpense;
	}

}
