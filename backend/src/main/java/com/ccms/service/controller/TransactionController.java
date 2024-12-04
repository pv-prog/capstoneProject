package com.ccms.service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ccms.service.model.Transaction;
import com.ccms.service.model.Transaction.TransactionDetail;
import com.ccms.service.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Transaction Controller", description = "Controller for managing Customers Creditcard Transactions")
@RestController
@RequestMapping("/api/customer/transactions")
public class TransactionController {

	@Autowired
	TransactionService transactionService;

	@Operation(summary = "Get all Transactions", description = "Show all transactions for every card associated with the given customer")
	@GetMapping("/{username}")
	public Transaction gettransactionsforuser(@PathVariable("username") String username) {

		System.out.println(transactionService.getTransactionsforuser(username));

		return transactionService.getTransactionsforuser(username);
	}

	@Operation(summary = "Retrieve the maximum expenses", description = "View the maximum expenses for all cards of the given customer in the last month")
	@GetMapping("/maxExpenses/lastMonth/{username}")
	public ResponseEntity<List<Map<String, Object>>> getMaxExpensesForLastMonth(@PathVariable String username,
			@RequestParam(required = false, defaultValue = "both") String status) {

		try {

			List<Map<String, Object>> maxExpenses = transactionService.getMaxExpensesForLastMonth(username, status);

			if (maxExpenses.isEmpty())

			{
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(maxExpenses);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(null);
		}

	}

	@Operation(summary = "Retrieve the high-value expenses", description = "View the high-value expenses for all cards of the given customer that exceed the specified threshold")
	@GetMapping("/highvalue/expenses/{username}")
	public ResponseEntity<Map<String, List<Map<String, TransactionDetail>>>> getMaxExpensesForLastMonth(
			@PathVariable String username, @RequestParam(required = false, defaultValue = "1") int limit,
			@RequestParam(required = false, defaultValue = "both") String status,
			@RequestParam double amountThreshold) {

		try {

			Map<String, List<Map<String, TransactionDetail>>> highvalueexpenses = transactionService
					.getHighValueExpensesForUser(username, limit, status, amountThreshold);

			if (highvalueexpenses.isEmpty()) {
				return ResponseEntity.noContent().build();
			}

			return ResponseEntity.ok(highvalueexpenses);

		} catch (

		IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(null);
		}
	}

	@Operation(summary = "Retrieve the last X expenses for all cards.", description = "View the last X expenses for all cards associated with the given customer")
	@GetMapping("/lastXTransactions/{username}")
	public ResponseEntity<Map<Integer, List<TransactionDetail>>> getLastXTransactionsForUser(
			@PathVariable("username") String username, @RequestParam(required = false, defaultValue = "1") int limit,
			@RequestParam(required = false, defaultValue = "both") String status) {

		System.out.println(username);

		try {
			Map<Integer, List<TransactionDetail>> transactions = transactionService
					.getLastXTransactionsForUser(username, limit, status);

			if (transactions.isEmpty()) {
				return ResponseEntity.noContent().build();
			}

			return ResponseEntity.ok(transactions);

		} catch (

		IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(null);
		}

	}

	@Operation(summary = "For Backend - Retrieve the last X expenses for all cards.", description = "View the last X expenses for all cards associated with the given customer")
	@GetMapping("/lastXExpenses/{username}")
	public ResponseEntity<List<Map<String, Object>>> getLastXExpensesForUser(@PathVariable("username") String username,
			@RequestParam(required = false, defaultValue = "1") int limit,
			@RequestParam(required = false, defaultValue = "both") String status) {

		System.out.println(username);

		try {
			List<Map<String, Object>> transactions = transactionService.getLastXExpensesForUser(username, limit,
					status);

			if (transactions.isEmpty()) {
				return ResponseEntity.noContent().build();
			}

			return ResponseEntity.ok(transactions);

		} catch (

		IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(null);
		}

	}

}
