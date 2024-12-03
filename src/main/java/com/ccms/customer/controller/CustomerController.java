package com.ccms.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.ccms.customer.constants.Constants;
import com.ccms.customer.model.Customer;
import com.ccms.customer.service.CustomerService;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Customer Controller", description = "Controller for managing Customers")
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

	@Autowired
	CustomerService customerService;

	@Operation(summary = "Get Customer Profile", description = "Show the profile details for the specified customer")
	@GetMapping("/{username}")
	public Customer getCustomer(@PathVariable("username") String username) {

		System.out.println(customerService.getCustomer(username));

		return customerService.getCustomer(username);

	}

	@Operation(summary = "Add Customer Profile", description = "Add a new customer")
	@PostMapping("/register")
	public Customer registerUser(@RequestBody Customer customer) {
		return customerService.addCustomer(customer);
	}

	@Operation(summary = "Authenticate Customer", description = "Authenticate Customer")
	@PostMapping("/authenticate")
	public String authenticateCustomer(@RequestParam String username, @RequestParam String password) {
		String token = customerService.authenticateCustomer(username, password);
		if (token != null) {
			return token;
		}
		throw new RuntimeException("Authentication failed");
	}

	@GetMapping("/protected")
	public String protectedApi() {
		return "You have access to the protected API!";

	}

	// Rest Template for Microservices
	@Autowired
	RestTemplate restTemplate;

	@Operation(summary = "Toggle Creditcards Status", description = "Update the status (Active/Inactive) of the credit cards for the given customer")
	@PutMapping("/toggle/{username}/{creditCardId}")
	public ResponseEntity<String> toggleCreditCardStatus(@PathVariable String username,
			@PathVariable int creditCardId) {

		try {

			String url = Constants.CREDIT_CARD_TOGGLE_SERVICE_URL + "{username}/{creditCardId}/toggle";

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, null, String.class, username,
					creditCardId);

			return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			// Handle client or server error (4xx/5xx)
			return ResponseEntity.status(ex.getStatusCode()).body("Error: " + ex.getResponseBodyAsString());
		} catch (Exception ex) {
			// Handle any other unexpected error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occurred: " + ex.getMessage());

		}

	}

}
