package com.ccms.service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccms.service.exception.InvalidUsernameFormatException;
import com.ccms.service.model.Transaction.TransactionDetail;
import com.ccms.service.model.TransactionWithCardId;
import com.ccms.service.service.TransactionService;
import com.ccms.service.utilities.Decodename;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Transaction Controller", description = "Controller for managing Customers Creditcard Transactions")
@RestController
@Validated
@RequestMapping("/api/customer/transactions")
public class TransactionController {

	private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

	@Autowired
	TransactionService transactionService;

	@Autowired
	private Decodename decodename;

	@Operation(summary = "Get all Transactions", description = "Show all transactions for every card associated with the given customer")
	@GetMapping("/{username}")
	public ResponseEntity<?> gettransactionsforuser(@PathVariable("username") String encodedusername,
			@RequestParam(required = false) Integer page, // No default value set
			@RequestParam(required = false) Integer size) { // No default value set

		// Handle validation failure explicitly

		String username = decodeUsername(encodedusername);

		// Log request parameters
		logger.info("Fetching transactions for user: {}, page: {}, limit: {}", username, page, size);

		// Set defaults for page and limit if they are not provided
		if (page == null) {
			page = 0; // Default to the first page
		}
		if (size == null) {
			size = 100; // Default to 100 transactions per page
		}

		try {
			// Set pagination details

			Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());

			// Fetch paginated transactions for the given user
			Page<TransactionWithCardId> transactions = transactionService.getTransactionsForUser(username, pageable);

			// Check if transactions are found

			if (transactions == null || transactions.isEmpty()) {

				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}

			// Prepare paginated response

			Map<String, Object> response = Map.of("content", transactions.getContent(), "totalElements",
					transactions.getTotalElements(), "totalPages", transactions.getTotalPages(), "currentPage",
					transactions.getNumber(), "size", transactions.getSize());

			System.out.println("response" + response);

			return ResponseEntity.ok(response);

		} catch (Exception e) {

			logger.error("An error occurred while fetching the credit card transactions", e);

			// Return a 500 Internal Server Error with the exception message

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error",
					List.of(Map.of("error", "An error occurred while fetching transactions: " + e.getMessage()))));
		}
	}

	@Operation(summary = "Retrieve the maximum expenses", description = "View the maximum expenses for all cards of the given customer in the last month")
	@GetMapping("/maxExpenses/lastMonth/{username}")
	public ResponseEntity<?> getMaxExpensesForLastMonth(@PathVariable("username") String encodedusername,
			@RequestParam(required = false, defaultValue = "both") String status,
			@RequestParam(required = false) Integer page, // No default value set
			@RequestParam(required = false) Integer size) { // No default value set)

		// Username validation: Check if it's null, empty, or exceeds max length

		String username = decodeUsername(encodedusername);

		// Set defaults for page and limit if they are not provided
		if (page == null) {
			page = 0; // Default to the first page
		}
		if (size == null) {
			size = 100; // Default to 100 transactions per page
		}

		// Status validation: Valid statuses are "enabled", "disabled", and "both"
		List<String> validStatuses = List.of("enabled", "disabled", "both");
		if (!validStatuses.contains(status)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List
					.of(Map.of("error", "Invalid status value. Valid values are 'enabled', 'disabled', or 'both'")));

		}

		try {

			// Set pagination details

			Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());

			// Fetch max expenses for the last month
			Page<Map<String, Object>> maxExpenses = transactionService.getMaxExpensesForLastMonth(username, status,
					pageable);

			// If no expenses are found, return a 204 No Content response
			if (maxExpenses.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}

			// Return the max expenses with a 200 OK response

			return ResponseEntity.ok(maxExpenses);

		} catch (IllegalArgumentException e) {

			return ResponseEntity.badRequest().body(List.of(Map.of("error", "Invalid argument provided")));
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(List.of(Map.of("error", "An unexpected error occurred. Please try again later")));
		}
	}

	@Operation(summary = "Retrieve the high-value expenses", description = "View the high-value expenses for all cards of the given customer that exceed the specified threshold")
	@GetMapping("/highvalue/expenses/{username}")
	public ResponseEntity<?> getHighValueExpenses(@PathVariable("username") String encodedusername,
			@RequestParam(required = false, defaultValue = "1") int limit,
			@RequestParam(required = false, defaultValue = "both") String status, @RequestParam double amountThreshold,
			@RequestParam(required = false) Integer page, // No default value set
			@RequestParam(required = false) Integer size) {

		// Validate username

		// Set defaults for page and limit if they are not provided
		if (page == null) {
			page = 0; // Default to the first page
		}

		if (size == null) {
			size = 100; // Default to 100 transactions per page
		}

		String username = decodeUsername(encodedusername);

		// Validate the limit to ensure it's a positive integer greater than 0
		if (limit <= 0) {
			return buildErrorResponse("The 'limit' parameter must be a positive integer greater than 0.");
		}
		// Validate amountThreshold
		if (amountThreshold <= 0) {
			return buildErrorResponse("Amount threshold must be a positive value");
		}

		// Validate status (should be either "enabled", "disabled", or "both")

		List<String> validStatuses = List.of("enabled", "disabled", "both");
		if (!validStatuses.contains(status)) {
			return buildErrorResponse("Invalid status value. Valid values are 'enabled', 'disabled', or 'both'");
		}

		try {
			// Call the service to get high-value expenses

			// Set pagination details

			Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());

			Map<String, Page<Map<String, String>>> highValueExpenses = transactionService
					.getHighValueExpensesForUser(username, limit, status, amountThreshold, pageable);

			// If no expenses are found, return 204 No Content
			if (highValueExpenses.isEmpty()) {

				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}

			// Return the high-value expenses with a 200 OK response
			return ResponseEntity.ok(highValueExpenses);

		} catch (IllegalArgumentException e) {
			// Log exception if needed
			return buildErrorResponse("Invalid argument provided");
		} catch (Exception e) {
			// Log exception for debugging
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					Map.of("error", List.of(Map.of("error", "An unexpected error occurred. Please try again later"))));
		}
	}

	@Operation(summary = "Retrieve the last X expenses for all cards.", description = "View the last X expenses for all cards associated with the given customer")
	@GetMapping("/lastXTransactions/{username}")
	public ResponseEntity<?> getLastXTransactionsForUser(@PathVariable("username") String encodedusername,
			@RequestParam(required = false, defaultValue = "1") int limit,
			@RequestParam(required = false, defaultValue = "both") String status,
			@RequestParam(required = false) Integer page, // No default value set
			@RequestParam(required = false) Integer size) {

		// Validate the username

		String username = decodeUsername(encodedusername);

		// Set defaults for page and limit if they are not provided
		if (page == null) {
			page = 0; // Default to the first page
		}

		if (size == null) {
			size = 100; // Default to 100 transactions per page
		}

		// Validate the limit to ensure it's a positive integer greater than 0
		if (limit <= 0) {
			return buildErrorResponse("The 'limit' parameter must be a positive integer greater than 0.");
		}
		// Validate status (should be either "enabled", "disabled", or "both")

		List<String> validStatuses = List.of("enabled", "disabled", "both");
		if (!validStatuses.contains(status)) {
			return buildErrorResponse("Invalid status value. Valid values are 'enabled', 'disabled', or 'both'");
		}

		try {

			// Set pagination details

			Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());

			Map<Integer, Page<TransactionDetail>> transactions = transactionService
					.getLastXTransactionsForUser(username, limit, status, pageable);

			if (transactions.isEmpty() || transactions == null) {

				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

			}

			return ResponseEntity.ok(transactions);

		} catch (

		IllegalArgumentException e) {
			return buildErrorResponse("Invalid argument provided");

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occurred. Please try again later");
		}

	}

	@Operation(summary = "For Backend - Retrieve the last X expenses for all cards.", description = "View the last X expenses for all cards associated with the given customer")
	@GetMapping("/lastXExpenses/{username}")
	public ResponseEntity<?> getLastXExpensesForUser(@PathVariable("username") String encodedusername,
			@RequestParam(required = false, defaultValue = "1") int limit,
			@RequestParam(required = false, defaultValue = "both") String status,
			@RequestParam(required = false) Integer page, // No default value set
			@RequestParam(required = false) Integer size) {

		// Validate the username

		String username = decodeUsername(encodedusername);

		// Set defaults for page and limit if they are not provided
		if (page == null) {
			page = 0; // Default to the first page
		}

		if (size == null) {
			size = 100; // Default to 100 transactions per page
		}

		// Validate the limit to ensure it's a positive integer greater than 0
		if (limit <= 0) {
			return buildErrorResponse("The 'limit' parameter must be a positive integer greater than 0.");
		}

		// Validate status (should be either "enabled", "disabled", or "both")
		List<String> validStatuses = List.of("enabled", "disabled", "both");
		if (!validStatuses.contains(status)) {
			return buildErrorResponse("Invalid status value. Valid values are 'enabled', 'disabled', or 'both'");
		}

		try {

			// Set pagination details
			Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());

			// Call the service to get the last X expenses
			Map<String, Object> transactions = transactionService.getLastXExpensesForUser(username, limit, status,
					pageable);

			// If no transactions are found, return 204 No Content
			if (transactions.isEmpty() || transactions == null) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}

			// Return the last X transactions with a 200 OK response
			return ResponseEntity.ok(transactions);

		} catch (IllegalArgumentException e) {
			// Log exception if needed
			return buildErrorResponse("Invalid argument provided");

		} catch (Exception e) {
			// Log exception for debugging
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(List.of(Map.of("error", "An unexpected error occurred. Please try again later")));
		}
	}

	// Helper method to decode username

	private String decodeUsername(String encodedusername) {
		try {
			return decodename.decodeUsername(encodedusername);
		} catch (InvalidUsernameFormatException e) {
			logger.error("Failed to decode username: {}...", encodedusername.substring(0, 3), e);
			throw e;
		}
	}

	// Helper method to standardize error responses
	private ResponseEntity<?> buildErrorResponse(String errorMessage) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(Map.of("error", List.of(Map.of("error", errorMessage))));
	}

}