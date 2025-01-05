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
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import com.ccms.service.model.Transaction;
import com.ccms.service.exception.CreditCardNotFoundException;
import com.ccms.service.exception.CustomerNotFoundException;
import com.ccms.service.exception.TransactionNotFoundException;
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

	private static final Logger logger = LoggerFactory.getLogger(TransactionServiceimpl.class);

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
		logger.info("Entering getMaxExpensesForLastMonth");

		List<CreditCardDetail> activeCreditCards = getActiveCreditCardsForUser(username, statusFilter);

		if (activeCreditCards.isEmpty())
			return Collections.emptyList();

		List<CreditCardTransaction> cardTransactions = getTransactionsForUser(username);

		if (cardTransactions.isEmpty())
			return Collections.emptyList();

		// Initialize a map to hold the maximum transaction amount for each card
		Map<Integer, List<TransactionWithCreditCardInfo>> allTransactionDetails = new HashMap<>();

		// Retrieve transactions for each active credit card

		for (CreditCardDetail creditCardDetail : activeCreditCards) {

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

		logger.debug("Date range: " + startOfLastMonth + " to " + endOfLastMonth);

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
					String decryptedCreditCardNumber = cardEnDecryption
							.decrypt(entry.getValue().get(0).getCreditCardNumber());

					entry.getValue().get(0).setCreditCardNumber(decryptedCreditCardNumber);

					String formattedCreditCardNumber = cardFormatter
							.maskCreditCardNumber(entry.getValue().get(0).getCreditCardNumber());

					expenseDetails.put("credit_card", formattedCreditCardNumber);
					expenseDetails.put("month", month);
					expenseDetails.put("amount", maxTransactionAmount);

					result.add(expenseDetails);

				} catch (Exception e) {

					logger.error("Error decrypting credit card number", e);
					continue;

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
		logger.info("Entering getHighValueExpensesForUser");

		List<CreditCardDetail> activeCreditCards = getActiveCreditCardsForUser(username, statusFilter);

		if (activeCreditCards.isEmpty())
			return Collections.emptyMap();

		List<CreditCardTransaction> cardTransactions = getTransactionsForUser(username);

		if (cardTransactions.isEmpty())
			return Collections.emptyMap();

		// Retrieve transactions for each active credit card

		Map<String, List<Map<String, String>>> allTransactionDetails = new LinkedHashMap();

		for (CreditCardDetail creditCardDetail : activeCreditCards) {

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
					String decryptedCreditCardNumber = cardEnDecryption.decrypt(creditCardDetail.getCreditCardNumber());

					String formattedCreditCardNumber = cardFormatter.maskCreditCardNumber(decryptedCreditCardNumber);

					allTransactionDetails.put(formattedCreditCardNumber, filteredTransactions);

				} catch (Exception e) {

					logger.error("Error decrypting credit card number for user: " + username, e);
					continue;
				}

			}
		}

		if (allTransactionDetails.isEmpty()) {
			logger.info("No high-value transactions found for any card.");
		}

		return allTransactionDetails;

	}

	// Customer wants to view last x number of expenses of all cards
	@Override
	public Map<Integer, List<TransactionDetail>> getLastXTransactionsForUser(String username, int limit,
			String statusFilter) {

		// Retrieve all active credit cards for the user

		logger.info("Entering getLastXTransactionsForUser");

		List<CreditCardDetail> activeCreditCards = getActiveCreditCardsForUser(username, statusFilter);

		if (activeCreditCards.isEmpty())
			return Collections.emptyMap();

		List<CreditCardTransaction> cardTransactions = getTransactionsForUser(username);

		if (cardTransactions.isEmpty())
			return Collections.emptyMap();

		// Retrieve transactions for each active credit card

		Map<Integer, List<TransactionDetail>> allTransactionDetails = new HashMap<>();

		for (CreditCardDetail creditCardDetail : activeCreditCards) {

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
		logger.info("Entering getLastXExpensesForUser");

		List<CreditCardDetail> activeCreditCards = getActiveCreditCardsForUser(username, statusFilter);

		if (activeCreditCards.isEmpty())
			return Collections.emptyList();

		List<CreditCardTransaction> cardTransactions = getTransactionsForUser(username);

		if (cardTransactions.isEmpty())
			return Collections.emptyList();

		List<Map<String, Object>> Maxexpense = new ArrayList<>();

		for (CreditCardDetail creditCardDetail : activeCreditCards) {

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
					String decryptedCreditCardNumber = cardEnDecryption.decrypt(creditCardDetail.getCreditCardNumber());

					String formattedCreditCardNumber = cardFormatter.maskCreditCardNumber(decryptedCreditCardNumber);

					creditCardData.put("credit_card", formattedCreditCardNumber);
					creditCardData.put("transactions", transactionList);

					Maxexpense.add(creditCardData);

				} catch (Exception e) {

					logger.error("Error decrypting credit card number for user: " + username, e);
					continue;
				}

			}
		}
		return Maxexpense;
	}

	// Utility method to get the active credit cards for the user

	private List<CreditCardDetail> getActiveCreditCardsForUser(String username, String statusFilter) {
		logger.info("Entering getActiveCreditCardsForUser");

		List<CreditCardDetail> activeCreditCards = new ArrayList<>();

		try {
			activeCreditCards = cardService.getAllCreditCardsForUser(username).getCreditcards();
			if (activeCreditCards == null || activeCreditCards.isEmpty()) {
				logger.error("No active credit cards found for user: {}", username);
				throw new CreditCardNotFoundException(username);
			}
		} 
		catch(CustomerNotFoundException e)
		{
			logger.error(e.getMessage());
			return Collections.emptyList(); 
		}
		catch (CreditCardNotFoundException e) {
			logger.error(e.getMessage());
			return Collections.emptyList(); 
		} catch (DataAccessException e) {
			logger.error("Database error while retrieving creditcards", e);
			return Collections.emptyList();
		} catch (Exception e) {
			logger.error("Error retrieving credit cards for user: {}", username);
			return Collections.emptyList(); // Handle other errors
		}
		return activeCreditCards.stream().filter(card -> {
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
	}

	// Utility method to get transactions for the user

	private List<CreditCardTransaction> getTransactionsForUser(String username) {

		List<CreditCardTransaction> cardTransactions = new ArrayList<>();
		try {
			cardTransactions = transactionRepository.findByUsername(username).getCreditcards().stream()
					.collect(Collectors.toList());
			if (cardTransactions == null || cardTransactions.isEmpty()) {
				logger.error("No transactions found for user: {}", username);
				throw new CreditCardNotFoundException(username);
			}
		} catch (TransactionNotFoundException e) {
			logger.error(e.getMessage());
			return Collections.emptyList(); // Handle user not found exception
		} catch (DataAccessException e) {
			logger.error("Database error while retrieving transactions", e);
			return Collections.emptyList();
		} catch (Exception e) {
			logger.error("Error retrieving Transactions for user: {}", username);
			return Collections.emptyList();
		}
		return cardTransactions;
	}

}
