package com.ccms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ccms.service.model.CreditCard;
import com.ccms.service.service.CreditCardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CreditCard Controller", description = "Controller for managing Customers Creditcard")
@RestController
@RequestMapping("/api/customer/creditcard")
public class CreditCardController {

	@Autowired
	private CreditCardService creditCardService;

	@Operation(summary = "Get all Creditcards", description = "Provides a list of all credit cards associated with the given customer")
	@GetMapping("/{username}")
	public CreditCard getcreditcardforuser(@PathVariable("username") String username) {

		System.out.println(creditCardService.getCreditcardforuser(username));

		return creditCardService.getCreditcardforuser(username);

	}

	@Operation(summary = "Add new Creditcards", description = "Add new credit cards for the specified customer")
	@PostMapping("/{username}")
	public ResponseEntity<CreditCard> addCreditCard(@PathVariable String username,
			@RequestBody CreditCard.CreditCardDetail creditCardDetail) {
		CreditCard updatedCreditCard = creditCardService.addCreditCard(username, creditCardDetail);
		return ResponseEntity.ok(updatedCreditCard);
	}

	@Operation(summary = "Toggle Creditcards Status", description = "Update the status (Active/Inactive) of the credit cards for the given customer")
	@PutMapping("/{username}/{creditCardId}/toggle")
	public ResponseEntity<String> toggleCreditCardStatus(@PathVariable String username,
			@PathVariable int creditCardId) {
		
		System.out.println("Toggle service");
		System.out.println(username);
		creditCardService.toggleCreditCardStatus(username, creditCardId);
		return ResponseEntity.ok("Credit card status toggled successfully.");
	}
}
