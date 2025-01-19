package com.ccms.customer.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ccms.customer.model.Customer;
import com.ccms.customer.model.JwtResponse;
import com.ccms.customer.repository.CustomerProjection;
import com.ccms.customer.repository.CustomerRepository;
import com.ccms.customer.service.CustomerService;
import com.ccms.customer.utilities.JwtUtil;


/**
 * Implementation of the {@link CustomerService} interface. This service handles operations related to authenticate Customer
 * and checks password matches to generate the JWT token.
 */

@Service
public class CustomerServiceimpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	JwtUtil jwtUtil;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	// Check if Add Customer
	@Override
	public Customer addCustomer(Customer customer) {
		customer.setPassword(passwordEncoder.encode(customer.getPassword()));
		return customerRepository.save(customer);
	}

	@Override
	public Customer getCustomer(String username) {

		return customerRepository.findByUsername(username);

	}

	@Override
	public Customer updateCustomer(Customer Customer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteCustomer(String username) {
		// TODO Auto-generated method stub

	}

	// Authenticate user and generate JWT

	@Override
	public ResponseEntity<?> authenticateCustomer(String username, String password) {
		// Try to fetch the customer from the repository
		Optional<CustomerProjection> customerOpt = customerRepository.findUsernameAndPasswordByUsername(username);

		// Check if the customer exists
		if (customerOpt.isPresent()) {
			CustomerProjection customer = customerOpt.get();

			// Compare the provided password with the stored (hashed) password using BCrypt
			if (passwordEncoder.matches(password, customer.getpassword())) {
				// Password matches, generate the token
				String token = jwtUtil.generateToken(username); // Generate token if authentication is successful

				return ResponseEntity.ok(new JwtResponse(token)); // Return the token in the response
			} else {
				// Invalid password
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
			}
		} else {
			// Customer not found
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found.");
		}

	}

}
