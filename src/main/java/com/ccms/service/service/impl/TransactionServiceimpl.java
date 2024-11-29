package com.ccms.service.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
	public Map<Integer, Double> getMaxExpensesForLastMonth(String username, String statusFilter) {

		// Step 1: Retrieve all active credit cards for the user

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

		// Step 2: Initialize a map to hold the maximum expenses for each card

		Map<Integer, List<TransactionDetail>> allTransactionDetails = new HashMap<>();

		// Step 3: Retrieve transactions for each active credit card

		System.out.println("Retrieve transactions for each active credit card");

		for (CreditCardDetail creditCardDetail : filteredCreditCards) {

			System.out.println("Processing credit card: " + creditCardDetail);

			// Fetch transactions for the current credit card

			List<CreditCardTransaction> cardTransactions = transactionRepository.findByUsername(username)
					.getCreditcards().stream().collect(Collectors.toList());

			System.out.println("Fetched transactions based on credit card ID");

			for (CreditCardTransaction creditCardTransaction : cardTransactions) {

				System.out.println("Processing transaction: " + creditCardTransaction);

				if (creditCardDetail.getCreditCardId() == creditCardTransaction.getCreditCardId()) {

					allTransactionDetails.computeIfAbsent(creditCardDetail.getCreditCardId(), k -> new ArrayList<>())
							.addAll(creditCardTransaction.getTransactions());

				}

			}

		}

		Map<Integer, Double> maxExpenses = new HashMap<>();

		// Calculate the date range for the last month

		LocalDate now = LocalDate.now();
		LocalDate startOfLastMonth = now.minusMonths(1).withDayOfMonth(1);
		LocalDate endOfLastMonth = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());

		System.out.println("Date range: " + startOfLastMonth + " to " + endOfLastMonth);

		for (Map.Entry<Integer, List<TransactionDetail>> entry : allTransactionDetails.entrySet()) {

			List<TransactionDetail> transactions = entry.getValue();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

			// Filter transactions within the date range

			if (!transactions.isEmpty()) {
				List<TransactionDetail> maxExpenseTransactions = transactions.stream().filter(t -> {
					LocalDate transactionDate = LocalDate.parse(t.getTransactionDate(), formatter);
					return (transactionDate.isEqual(startOfLastMonth) || transactionDate.isAfter(startOfLastMonth))
							&& (transactionDate.isEqual(endOfLastMonth) || transactionDate.isBefore(endOfLastMonth));
				}).collect(Collectors.toList());

				// Calculate the maximum expense for the last month
				double maxExpense = 0.0;

				double totalExpense = maxExpenseTransactions.stream()
						.mapToDouble(TransactionDetail::getTransactionAmount).sum();

				// Update the maximum expense if necessary
				if (totalExpense > maxExpense) {
					maxExpense = totalExpense;

					// Add the limited list back to the new map
				}
				maxExpenses.put(entry.getKey(), maxExpense);
			}

		}

		System.out.println("Maximum expenses for the last month: " + maxExpenses);
		return maxExpenses;

	}

	// Expenses more than the amount (xxxxx)

	@Override
	public Map<Integer, List<TransactionDetail>> getHighValueExpensesForUser(String username, String statusFilter,
			double amountThreshold) {

		// Step 1: Retrieve all active credit cards for the user
		System.out.println("Entering getHighValueExpensesForUser");

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

		// Step 2: Retrieve transactions for each active credit card
		Map<Integer, List<TransactionDetail>> allTransactionDetails = new HashMap<>();
		System.out.println("Retrieve transactions for each active credit card");

		for (CreditCardDetail creditCardDetail : filteredCreditCards) {
			System.out.println("Processing credit card: " + creditCardDetail);

			// Fetch transactions for the current credit card
			List<CreditCardTransaction> cardTransactions = transactionRepository.findByUsername(username)
					.getCreditcards().stream().collect(Collectors.toList());

			System.out.println("Fetched transactions based on credit card ID");

			for (CreditCardTransaction creditCardTransaction : cardTransactions) {
				System.out.println("Processing transaction: " + creditCardTransaction);

				if (creditCardDetail.getCreditCardId() == creditCardTransaction.getCreditCardId()) {

					// Filter transactions based on the amount threshold

					List<TransactionDetail> filteredTransactions = creditCardTransaction.getTransactions().stream()
							.filter(transaction -> transaction.getTransactionAmount() > amountThreshold) // ---- Check
																											// if amount
																											// is
																											// greater
																											// than the
																											// threshold
							.collect(Collectors.toList());

					allTransactionDetails.computeIfAbsent(creditCardDetail.getCreditCardId(), k -> new ArrayList<>())
							.addAll(filteredTransactions);
				}
			}
		}

		System.out.println("All transaction details: " + allTransactionDetails);

		// Now list all the transactions
		Map<Integer, List<TransactionDetail>> highValueTransactionDetails = new HashMap<>();

		for (Map.Entry<Integer, List<TransactionDetail>> entry : allTransactionDetails.entrySet()) {
			List<TransactionDetail> transactions = entry.getValue();

			// Sort and limit the transactions based on date

			if (!transactions.isEmpty()) {
				List<TransactionDetail> highValueExpenses = transactions.stream()
						.sorted(Comparator.comparing(TransactionDetail::getTransactionDate).reversed())
						.collect(Collectors.toList());

				// Add the limited list back to the new map
				highValueTransactionDetails.put(entry.getKey(), highValueExpenses);

				if (highValueTransactionDetails.isEmpty()) {
					System.out.println("No high-value transactions found for any card.");
				}
			}

		}
		System.out.println("Limited transaction details: " + highValueTransactionDetails);

		return highValueTransactionDetails; // Return the map with limited transactions
	}

	// Customer wants to view last x number of expenses of all cards
	@Override
	public Map<Integer, List<TransactionDetail>> getLastXTransactionsForUser(String username, int limit,
			String statusFilter) {

		// Step 1: Retrieve all active credit cards for the user

		System.out.println("Entering getLastXTransactionsForUser");

//		List<CreditCardDetail> activecreditcards = cardService.getallCreditcardsforuser(username).getCreditcards()
//				.stream().filter(c -> c.getStatus().equalsIgnoreCase("enabled")).collect(Collectors.toList());

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

		// Step 2: Retrieve transactions for each active credit card

		Map<Integer, List<TransactionDetail>> allTransactionDetails = new HashMap<>();

		System.out.println("Retrieve transactions for each active credit card");

		for (CreditCardDetail creditCardDetail : filteredCreditCards) {

			System.out.println("Processing credit card: " + creditCardDetail);

			// Fetch transactions for the current credit card

			List<CreditCardTransaction> cardTransactions = transactionRepository.findByUsername(username)
					.getCreditcards().stream().collect(Collectors.toList());

			System.out.println("Fetched transactions based on credit card ID");

			for (CreditCardTransaction creditCardTransaction : cardTransactions) {

				System.out.println("Processing transaction: " + creditCardTransaction);

				if (creditCardDetail.getCreditCardId() == creditCardTransaction.getCreditCardId()) {

					allTransactionDetails.computeIfAbsent(creditCardDetail.getCreditCardId(), k -> new ArrayList<>())
							.addAll(creditCardTransaction.getTransactions());

				}

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

}
