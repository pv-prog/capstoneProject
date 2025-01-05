package com.ccms.service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ccms.service.exception.InvalidUsernameFormatException;
import com.ccms.service.kafka.CustomerKafkaProducer;
import com.ccms.service.model.Customer;
import com.ccms.service.service.CustomerService;
import com.ccms.service.utilities.Decodename;
import com.ccms.service.utilities.ErrorResponse;
import com.ccms.service.utilities.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Customer Controller", description = "Controller for managing Customers")
@RestController
@Validated
@RequestMapping("/api/customer")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerKafkaProducer customerKafkaProducer;

    @Autowired
    private Decodename decodename;

    @Operation(summary = "Get Customer Profile", description = "Show the profile details for the specified customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved customer profile"),
        @ApiResponse(responseCode = "400", description = "Invalid username encoding or malformed request"),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{username}")
    public ResponseEntity<Object> getCustomer(@PathVariable("username") String encodedusername) {

        String username;
        
        try {
            username = decodename.decodeUsername(encodedusername);
        } catch (InvalidUsernameFormatException e) {
        	logger.error("Failed to decode username: {}...", encodedusername.substring(0, 3), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            		.body(new ErrorResponse("Invalid username encoding", e.getMessage()));
        }

        try {
            // Fetch the customer profile
            Customer customer = customerService.getCustomer(username);

            // If no customer is found, return 404 Not Found
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                		 .body(new ErrorResponse("Customer not found", "No customer found for username: " + username));
            }

            // Return the customer profile data if found
            return ResponseEntity.ok(new SuccessResponse<>(customer));

        } catch (Exception e) {
            logger.error("An error occurred while fetching the customer profile for username: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            		.body(new ErrorResponse("An error occurred while fetching the customer profile", e.getMessage()));
        }
    }
}
