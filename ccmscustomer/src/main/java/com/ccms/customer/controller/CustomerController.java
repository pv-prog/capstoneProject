package com.ccms.customer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ccms.customer.constants.Constants;
import com.ccms.customer.dto.AuthenticationRequest;
import com.ccms.customer.exception.InvalidUsernameException;
import com.ccms.customer.exception.InvalidUsernameFormatException;
import com.ccms.customer.exception.UnauthorizedException;
import com.ccms.customer.model.Customer;
import com.ccms.customer.service.CustomerService;
import com.ccms.customer.utilities.Decodename;
import com.ccms.customer.utilities.JwtUtil;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for managing customer-related operations. Handles requests like
 * retrieving customer profile, authenticating customer, and updating credit
 * card status.
 */

@Tag(name = "Customer Controller", description = "Controller for managing Customers")
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private Decodename decodename;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private JwtUtil jwtUtil;

	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	@Operation(summary = "Get Customer Profile", description = "Show the profile details for the specified customer")
	@GetMapping("/{username}")
	public ResponseEntity<?> getCustomer(@PathVariable("username") String encodedusername) {

		String username;

		try {
			username = decodename.decodeUsername(encodedusername);
		} catch (InvalidUsernameFormatException e) {
			logger.error("Failed to decode username: {}...", encodedusername.substring(0, 3), e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid username encoding");
		}

		try {
			Customer customer = customerService.getCustomer(username);

			if (customer == null) {
				// Customer not found
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Customer with username '" + username + "' not found.");
			}

			logger.info("Successfully retrieved customer profile for username: {}", username);

			return ResponseEntity.ok(customer); // Return the customer details with 200 OK status

		} catch (Exception e) {
			// Handle unexpected errors (e.g., database issues, etc.)
			logger.error("Error retrieving customer profile for username: {}", username, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while retrieving the customer profile.");
		}
	}

	@Operation(summary = "Authenticate Customer", description = "Authenticate Customer")
	@PostMapping("/authenticate")
	public ResponseEntity<?> authenticateCustomer(@RequestBody AuthenticationRequest authenticationRequest) {

		String username;

		try {
			// Attempt to decode the username from Base64 encoding

			username = decodename.decodeUsername(authenticationRequest.getUsername());
		} catch (InvalidUsernameException e) {

			// Log the error and return a BAD_REQUEST response if the username is not Base64
			// encoded correctly
			logger.error("Invalid Base64 encoding for username: {}", authenticationRequest.getUsername(), e);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Base64 encoding for username.");
		} catch (Exception e) {

			// Catch any other exceptions and log them
			logger.error("Unexpected error while decoding username: {}", authenticationRequest.getUsername(), e);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Internal server error: " + e.getMessage());
		}

		// Step 2: Proceed with authentication if username decoding is successful
		try {
			// Call the service method with the decoded username
			ResponseEntity<?> tokenResponse = customerService.authenticateCustomer(username,
					authenticationRequest.getPassword());

			// If token is valid, return it
			if (tokenResponse != null) {
				return tokenResponse; // Send the token back to the user
			} else {
				// If authentication fails, return Unauthorized response
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("Authentication failed: Invalid username or password");
			}
		} catch (Exception ex) {
			// Handle other unexpected exceptions
			logger.error("Unexpected error during authentication for username: {}", username, ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Internal server error: " + ex.getMessage());
		}
	}

	@GetMapping("/protected")
	public String protectedApi() {
		return "You have access to the protected API!";

	}

	@Operation(summary = "Toggle Creditcard Status", description = "Update the status (Active/Inactive) of the credit cards for the given customer")
	@PutMapping("/toggle/{username}/{creditCardId}")
	@CircuitBreaker(name = "CcmsService", fallbackMethod = "orderFallback")
	public ResponseEntity<String> toggleCreditCardStatus(@PathVariable("username") String encodedusername,
			@PathVariable int creditCardId, @RequestHeader("Authorization") String authorizationHeader) { // Get the token from the  header
																									 
		String username;

		try {
			// Step 1: Decode the username from Base64 encoding (if needed)
			username = decodename.decodeUsername(encodedusername); // Decode the Base64 encoded username

		} catch (InvalidUsernameException e) {
			// Log the error and return a BAD_REQUEST response if the username is not Base64
			// encoded correctly
			logger.error("Invalid Base64 encoding for username: {}", encodedusername, e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Base64 encoding for username.");
		} catch (Exception e) {
			// Catch any other exceptions and log them
			logger.error("Unexpected error while decoding username: {}", encodedusername, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Internal server error: " + e.getMessage());
		}

		try {
			// Step 1: Extract and validate the token
			String token = authorizationHeader.substring(7); // Remove "Bearer " prefix

			// Validate token
			boolean isValid = jwtUtil.validateToken(token, username);

			// Extract username from token
			String tokenUsername = jwtUtil.extractUsername(token);

			// Step 2: Check if the username from the token matches the one in the path
			if (!username.equals(tokenUsername)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body("Token username does not match request username.");
			}

			// Step 3: Construct the URL for the external service
			String url = UriComponentsBuilder.fromHttpUrl(Constants.CREDIT_CARD_TOGGLE_SERVICE_URL)
					.path("/{username}/{creditCardId}/toggle").buildAndExpand(encodedusername, creditCardId)
					.toUriString();

			// Create an empty HttpEntity since we don't need a body for this request
			// Critical section - synchronize external service call
			synchronized (this) {
				HttpEntity<Void> requestEntity = new HttpEntity<>(null);

				// Log the request for debugging purposes
				logger.info("Requesting to toggle credit card status with URL: {}", url);

				// Step 4: Make the PUT request using RestTemplate
				ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity,
						String.class);

				// Log the response
				logger.info("Received response: Status code = {} and Body = {}", response.getStatusCode(),
						response.getBody());

				// Return the response from the external service
				return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
			}
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			// Handle client or server error (4xx/5xx)
			logger.error("HTTP error occurred: Status code = {} and Body = {}", ex.getStatusCode(),
					ex.getResponseBodyAsString());
			return ResponseEntity.status(ex.getStatusCode()).body("Error: " + ex.getResponseBodyAsString());
		} catch (Exception ex) {
			// Handle any other unexpected error
			logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occurred: " + ex.getMessage());
		}
	}

	// Fallback method for circuit breaker (matching signature)
	public ResponseEntity<String> orderFallback(String encodedusername, int creditCardId, String authorizationHeader,
			Throwable e) {
		// Log the fallback being triggered
		logger.error("Fallback triggered for username: {} and credit card ID: {} due to exception: {}", encodedusername,
				creditCardId, e.getMessage());

		// Return a SERVICE_UNAVAILABLE status to clearly indicate the service is down
		return new ResponseEntity<String>("CCMS Service is down. Please try later.", HttpStatus.SERVICE_UNAVAILABLE);
	}

}
