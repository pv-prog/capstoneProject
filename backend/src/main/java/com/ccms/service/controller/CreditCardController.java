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

/**
 * Controller responsible for handling customer credit card operations. This
 * includes fetching credit cards, adding new cards, and toggling card statuses.
 * 
 * <p>
 * Provides a REST API for customers to manage their credit cards with
 * operations such as:
 * </p>
 * <ul>
 * <li>Get all credit cards for a user</li>
 * <li>Add a new credit card</li>
 * <li>Toggle the status of a credit card</li>
 * </ul>
 */

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

	/**
	 * Reusable map used for logging additional context information. This map stores
	 * key-value pairs for logging purposes to provide more context on operations
	 * such as credit card addition, deletion, or updates.
	 */

	Map<String, Object> jsonLogMap = new LinkedHashMap<>();

	/**
	 * Retrieves all credit cards for a given customer.
	 *
	 * @param encodedusername The encoded username of the customer whose credit
	 *                        cards are to be retrieved.
	 * @param showFullNumber  Flag indicating whether to show full credit card
	 *                        numbers or masked numbers.
	 * @return A {@link ResponseEntity} containing the list of credit cards or an
	 *         error message.
	 */

	@Operation(summary = "Get all Creditcards", description = "Provides a list of all credit cards associated with the given customer")
	@GetMapping("/listcreditcards/{username}")
	public ResponseEntity<Object> getCreditCardsForUser(@PathVariable("username") String encodedusername,
			@RequestParam boolean showFullNumber) {

		String username = decodeUsername(encodedusername);

		try {

			CreditCard creditCards = creditCardService.getCreditCardForUser(username, showFullNumber);

			if (creditCards == null) {
				return createErrorResponse(HttpStatus.NOT_FOUND, "Not Found",
						"No credit cards found for user: " + username);
			}

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

	/**
	 * Adds a new credit card for the specified customer.
	 *
	 * @param encodedusername  The encoded username of the customer to whom the
	 *                         credit card will be added.
	 * @param creditCardDetail The details of the credit card to be added.
	 * @return A {@link ResponseEntity} containing the added credit card details or
	 *         an error message.
	 */

	@Operation(summary = "Add new Creditcards", description = "Add new credit cards for the specified customer")
	@PostMapping("/addcreditcard/{username}")
	public ResponseEntity<Object> addCreditCard(@PathVariable("username") String encodedusername,
			@RequestBody CreditCard.CreditCardDetail creditCardDetail) {

		String username = decodeUsername(encodedusername);

		if (creditCardDetail == null) {
			return createErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", "Credit card details cannot be null");
		}

		try {

			CreditCardDetail updatedCreditCard = creditCardService.addCreditCard(username, creditCardDetail);

			// Log the success to Kafka
			String message = "Credit card added successfully";

			CreditCardLogUtil.logCreditCard(jsonLogMap, "success", message, username,
					creditCardDetail.getCreditCardId(), creditCardKafkaProducer);

			return ResponseEntity.status(HttpStatus.CREATED).body(updatedCreditCard);

		} catch (IllegalArgumentException ex) {

			logger.error("Error occurred while adding credit card for user: {}, Credit Card ID: {}", username,
					creditCardDetail.getCreditCardId(), ex);

			String message = "Error occurred while adding the credit card -> " + ex.getMessage();

			CreditCardLogUtil.logCreditCard(jsonLogMap, "failure", message, username,
					creditCardDetail.getCreditCardId(), creditCardKafkaProducer);

			return createErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
		}

		catch (DuplicateCreditCardException ex) {

			logger.error("Credit card already exist and associated with this user: {}, Credit Card ID: {}", username,
					creditCardDetail.getCreditCardId(), ex);

			String message = "Credit card already exist and associated with this user -> " + ex.getMessage();

			CreditCardLogUtil.logCreditCard(jsonLogMap, "failure", message, username,
					creditCardDetail.getCreditCardId(), creditCardKafkaProducer);

			return createErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());

		}

		catch (Exception e) {

			logger.error("An unexpected error occurred while adding credit card for user: {}, Credit Card ID: {}",
					username, creditCardDetail.getCreditCardId(), e);

			String message = "Error occurred while adding the credit card" + e.getMessage();

			CreditCardLogUtil.logCreditCard(jsonLogMap, "failure", message, username,
					creditCardDetail.getCreditCardId(), creditCardKafkaProducer);

			return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
					"An unexpected error occurred");
		}
	}

	/**
	 * Toggles the status (active/inactive) of a specified credit card for the given
	 * customer.
	 *
	 * @param encodedusername The encoded username of the customer whose credit card
	 *                        status is to be updated.
	 * @param creditCardId    The ID of the credit card whose status is to be
	 *                        toggled.
	 * @return A {@link ResponseEntity} containing a success or failure message.
	 */

	@Operation(summary = "Toggle Creditcards Status", description = "Update the status (Active/Inactive) of the credit cards for the given customer")
	@PutMapping("/togglecreditcard/{username}/{creditCardId}/toggle")
	public ResponseEntity<Object> toggleCreditCardStatus(@PathVariable("username") String encodedusername,
			@PathVariable("creditCardId") int creditCardId) {

		String username = decodeUsername(encodedusername);

		try {

			boolean statusUpdated = creditCardService.toggleCreditCardStatus(username, creditCardId);

			if (!statusUpdated) {

				String message = "Credit card status not toggled -> ID : " + creditCardId + " was not found for user: "
						+ username;

				CreditCardLogUtil.logCreditCard(jsonLogMap, "failure", message, username, creditCardId,
						creditCardKafkaProducer);

				return createErrorResponse(HttpStatus.NOT_FOUND, "Not Found", message);

			}

			String message = "Credit card status toggled successfully";

			CreditCardLogUtil.logCreditCard(jsonLogMap, "success", message, username, creditCardId,
					creditCardKafkaProducer);

			return ResponseEntity.ok(message);

		} catch (CreditCardNotFoundException ex) {

			String message = "Credit card status not toggled -> ID : " + creditCardId + " was not found for user: "
					+ username;

			CreditCardLogUtil.logCreditCard(jsonLogMap, "failure", message, username, creditCardId,
					creditCardKafkaProducer);

			return createErrorResponse(HttpStatus.NOT_FOUND, "Not Found", message);
		}

		catch (Exception e) {

			logger.error("An error occurred while toggling the credit card status", e);

			return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
					"An error occurred while toggling the credit card status: " + e.getMessage());
		}
	}

	/**
	 * Decodes the encoded username
	 */

	private String decodeUsername(String encodedusername) {
		try {
			return decodename.decodeUsername(encodedusername);
		} catch (InvalidUsernameFormatException e) {
			logger.error("Failed to decode username: {}...", encodedusername.substring(0, 3), e);
			throw e;
		}
	}

	/**
	 * Helper method for error responses
	 */

	private ResponseEntity<Object> createErrorResponse(HttpStatus status, String error, String message) {
		return ResponseEntity.status(status).body(new ErrorResponse(error, message));
	}
}