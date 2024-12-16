package com.ccms.service.controller;

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
import com.ccms.service.model.CreditCard;
import com.ccms.service.model.CreditCard.CreditCardDetail;
import com.ccms.service.service.CreditCardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CreditCard Controller", description = "Controller for managing Customers Creditcard")
@RestController
@Validated
@RequestMapping("/api/customer/creditcard")
public class CreditCardController {

	@Autowired
	private CreditCardService creditCardService;

	@Operation(summary = "Get all Creditcards", description = "Provides a list of all credit cards associated with the given customer")
	@GetMapping("/listcreditcards/{username}")
	public ResponseEntity<?> getCreditCardsForUser(@PathVariable("username") String username,@RequestParam boolean showFullNumber) {

		// Handle validation failure explicitly

		if (username == null || username.trim().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username cannot be null or empty");
		}

		if (username.length() > 25) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username must not exceed 25 characters");
		}

		try {
			// Call the service layer to fetch the credit cards for the given user
			CreditCard creditCards = creditCardService.getCreditcardforuser(username,showFullNumber);

			// If no credit cards are found, return 404 Not Found
			if (creditCards == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No credit cards found for user: " + username);
			}

			// Return the credit card data if found
			return ResponseEntity.ok(creditCards);

		} catch (Exception e) {
			// Log the exception for debugging
			e.printStackTrace();

			// Return a 500 Internal Server Error with the exception message
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while fetching the credit cards: " + e.getMessage());
		}
	}

	@Operation(summary = "Add new Creditcards", description = "Add new credit cards for the specified customer")
	@PostMapping("/addcreditcard/{username}")
	public ResponseEntity<?> addCreditCard(@PathVariable("username") String username,
			@RequestBody CreditCard.CreditCardDetail creditCardDetail) {

		// Handle validation for username explicitly

		if (username == null || username.trim().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username cannot be null or empty");
		}

		if (username.length() > 25) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username must not exceed 25 characters");
		}

		// Handle validation for credit card details
		if (creditCardDetail == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Credit card details cannot be null");
		}

		try {
			// Call the service layer to add the credit card
			CreditCardDetail updatedCreditCard = creditCardService.addCreditCard(username, creditCardDetail);

			// If the operation is successful, return a 201 Created status with the updated
			// credit card
			return ResponseEntity.status(HttpStatus.CREATED).body(updatedCreditCard);

		}  catch (IllegalArgumentException ex) {
	 
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                             .body("An error occurred while adding the credit card: " + ex.getMessage());
	    }
		
		catch (Exception e) {
			// Log the exception for debugging
			e.printStackTrace();

			// Return a 500 Internal Server Error with the exception message
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while adding the credit card: " + e.getMessage());
		}
	}

	@Operation(summary = "Toggle Creditcards Status", description = "Update the status (Active/Inactive) of the credit cards for the given customer")
	@PutMapping("/togglecreditcard/{username}/{creditCardId}/toggle")
	public ResponseEntity<String> toggleCreditCardStatus(@PathVariable("username") String username,
			@PathVariable("creditCardId") int creditCardId) {

		// Handle validation for username explicitly
		if (username == null || username.trim().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username cannot be null or empty");
		}

		if (username.length() > 25) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username must not exceed 25 characters");
		}

		try {
			// Call the service to toggle the credit card status
			boolean statusUpdated = creditCardService.toggleCreditCardStatus(username, creditCardId);

			// If the status was not updated (e.g., invalid credit card ID), return a 404
			// Not Found
			if (!statusUpdated) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Credit card with ID " + creditCardId + " not found for user: " + username);
			}

			// Return a success response with a 200 OK status
			return ResponseEntity.ok("Credit card status toggled successfully.");

		} catch (Exception e) {
			// Log the exception for debugging
			e.printStackTrace();

			// Return a 500 Internal Server Error with the exception message
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while toggling the credit card status: " + e.getMessage());
		}
	}

}
