package com.ccms.service.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccms.service.exception.CreditCardNotFoundException;
import com.ccms.service.exception.DuplicateCreditCardException;
import com.ccms.service.exception.InvalidUsernameFormatException;
import com.ccms.service.kafka.CreditCardKafkaProducer;
import com.ccms.service.model.CreditCard;
import com.ccms.service.model.CreditCard.CreditCardDetail;
import com.ccms.service.service.CreditCardService;
import com.ccms.service.utilities.CreditCardLogUtil;
import com.ccms.service.utilities.Decodename;
import com.ccms.service.utilities.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CreditCard Controller", description = "Controller for managing Customers Creditcard")
@RestController
@Validated
@RequestMapping("/api/customer/creditcard")
public class CreditCardController {

	private static final Logger logger = LoggerFactory.getLogger(CreditCardController.class);

	@Autowired
	private CreditCardService creditCardService;

	@Autowired
	private CreditCardKafkaProducer creditCardKafkaProducer;

	@Autowired
	private Decodename decodename;

	// Reusable map for logging

	Map<String, Object> jsonLogMap = new LinkedHashMap<>();

	@Operation(summary = "Get all Creditcards", description = "Provides a list of all credit cards associated with the given customer")
	@GetMapping("/listcreditcards/{username}")
	public ResponseEntity<Object> getCreditCardsForUser(@PathVariable("username") String encodedusername,
			@RequestParam boolean showFullNumber) {

		// Handle validation failure explicitly

		String username = decodeUsername(encodedusername);

		try {
			// Call the service layer to fetch the credit cards for the given user
			CreditCard creditCards = creditCardService.getCreditCardForUser(username, showFullNumber);

			// If no credit cards are found, return 404 Not Found
			if (creditCards == null) {
				return createErrorResponse(HttpStatus.NOT_FOUND, "Not Found",
						"No credit cards found for user: " + username);
			}
			// Return the credit card data if found
			return ResponseEntity.ok(creditCards);

		} catch (CreditCardNotFoundException e) {
			logger.error("An error occurred while fetching the credit cards", e);

			return createErrorResponse(HttpStatus.NOT_FOUND, "Not Found",
					"No credit cards found for user: " + username);

		}

		catch (Exception e) {
			logger.error("An error occurred while fetching the credit cards", e);
			return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
					"An error occurred while fetching the credit cards: " + e.getMessage());
		}
	}

	@Operation(summary = "Add new Creditcards", description = "Add new credit cards for the specified customer")
	@PostMapping("/addcreditcard/{username}")
	public ResponseEntity<Object> addCreditCard(@PathVariable("username") String encodedusername,
			@RequestBody CreditCard.CreditCardDetail creditCardDetail) {

		// Handle validation for username explicitly

		String username = decodeUsername(encodedusername);

		// Handle validation for credit card details
		if (creditCardDetail == null) {
			return createErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", "Credit card details cannot be null");
		}

		try {
			// Call the service layer to add the credit card
			CreditCardDetail updatedCreditCard = creditCardService.addCreditCard(username, creditCardDetail);

			// If the operation is successful, return a 201 Created status with the updated
			// credit card

			// Send credit card log to Kafka
			String message = "Credit card added successfully";

			// Use the common method to log success
			CreditCardLogUtil.logCreditCard(jsonLogMap, "success", message, username,
					creditCardDetail.getCreditCardId(), creditCardKafkaProducer);

			return ResponseEntity.status(HttpStatus.CREATED).body(updatedCreditCard);

		} catch (IllegalArgumentException ex) {

			// Log the exception for validation failure

			logger.error("Error occurred while adding credit card for user: {}, Credit Card ID: {}", username,
					creditCardDetail.getCreditCardId(), ex);

			// Send credit card log to Kafka
			String message = "Error occurred while adding the credit card -> " + ex.getMessage();

			// Use the common method to log success
			CreditCardLogUtil.logCreditCard(jsonLogMap, "failure", message, username,
					creditCardDetail.getCreditCardId(), creditCardKafkaProducer);

			return createErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
		}
		
		catch (DuplicateCreditCardException ex) {

			// Log the exception for validation failure

			logger.error("Credit card already exist and associated with this user: {}, Credit Card ID: {}", username,
					creditCardDetail.getCreditCardId(), ex);

			// Send credit card log to Kafka
			String message = "Credit card already exist and associated with this user -> " + ex.getMessage();

			// Use the common method to log success
			CreditCardLogUtil.logCreditCard(jsonLogMap, "failure", message, username,
					creditCardDetail.getCreditCardId(), creditCardKafkaProducer);

			return createErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
			
		}

		catch (Exception e) {
			// Log the exception for debugging
			logger.error("An unexpected error occurred while adding credit card for user: {}, Credit Card ID: {}",
					username, creditCardDetail.getCreditCardId(), e);

			String message = "Error occurred while adding the credit card" + e.getMessage();

			// Use the common method to log success
			CreditCardLogUtil.logCreditCard(jsonLogMap, "failure", message, username,
					creditCardDetail.getCreditCardId(), creditCardKafkaProducer);
			// Return a 500 Internal Server Error with the exception message
			return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
					"An unexpected error occurred");
		}
	}

	@Operation(summary = "Toggle Creditcards Status", description = "Update the status (Active/Inactive) of the credit cards for the given customer")
	@PutMapping("/togglecreditcard/{username}/{creditCardId}/toggle")
	public ResponseEntity<Object> toggleCreditCardStatus(@PathVariable("username") String encodedusername,
			@PathVariable("creditCardId") int creditCardId) {

		String username = decodeUsername(encodedusername);

		try {
			// Call the service to toggle the credit card status
			boolean statusUpdated = creditCardService.toggleCreditCardStatus(username, creditCardId);

			// If the status was not updated (e.g., invalid credit card ID), return a 404
			// Not Found
			if (!statusUpdated) {

				String message = "Credit card status not toggled -> ID : " + creditCardId + " was not found for user: "
						+ username;

				// Use the common method to log failure
				CreditCardLogUtil.logCreditCard(jsonLogMap, "failure", message, username, creditCardId,
						creditCardKafkaProducer);

				return createErrorResponse(HttpStatus.NOT_FOUND, "Not Found", message);

			}

			// Send credit card log to Kafka

			String message = "Credit card status toggled successfully";

			CreditCardLogUtil.logCreditCard(jsonLogMap, "success", message, username, creditCardId,
					creditCardKafkaProducer);

			// Return a success response with a 200 OK status
			return ResponseEntity.ok(message);

		} catch (CreditCardNotFoundException ex) {

			// Send credit card log to Kafka
			String message = "Credit card status not toggled -> ID : " + creditCardId + " was not found for user: "
					+ username;

			// Use the common method to log success
			CreditCardLogUtil.logCreditCard(jsonLogMap, "failure", message, username,
					creditCardId, creditCardKafkaProducer);

			return createErrorResponse(HttpStatus.NOT_FOUND, "Not Found", message);
		}
		
		catch (Exception e) {
			// Log the exception for debugging
			logger.error("An error occurred while toggling the credit card status", e);

			return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
					"An error occurred while toggling the credit card status: " + e.getMessage());
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

	// Helper method to handle error responses
	private ResponseEntity<Object> createErrorResponse(HttpStatus status, String error, String message) {
		return ResponseEntity.status(status).body(new ErrorResponse(error, message));
	}
}