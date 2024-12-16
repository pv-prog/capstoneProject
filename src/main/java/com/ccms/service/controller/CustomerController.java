package com.ccms.service.controller;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ccms.service.model.Customer;
import com.ccms.service.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Customer Controller", description = "Controller for managing Customers")
@RestController
@Validated
@RequestMapping("/api/customer")
public class CustomerController {

	@Autowired
	CustomerService customerService;

	@Operation(summary = "Get Customer Profile", description = "Show the profile details for the specified customer")
	@GetMapping("/{encodedusername}")
	public ResponseEntity<?> getCustomer(@PathVariable("encodedusername") String encodedusername) {

		// Handle validation failure explicitly
		
		String username = new String(Base64.getDecoder().decode(encodedusername));

		if (username == null || username.trim().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username cannot be null or empty");
		}

		if (username.length() > 25) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username must not exceed 25 characters");
		}

		try {
			// Call the service layer to fetch the customer profile
			Customer customer = customerService.getCustomer(username);

			// If no customer is found, return 404 Not Found
			if (customer == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found for username: " + username);
			}

			// Return the customer profile data if found
			return ResponseEntity.ok(customer);

		} catch (Exception e) {
			// Log the exception for debugging
			e.printStackTrace();

			// Return a 500 Internal Server Error with the exception message
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while fetching the customer profile: " + e.getMessage());
		}
	}

}
