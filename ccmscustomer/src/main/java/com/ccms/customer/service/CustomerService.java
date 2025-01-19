package com.ccms.customer.service;

import org.springframework.http.ResponseEntity;

import com.ccms.customer.model.Customer;

/**
 * Service interface for managing customer-related operations.
 * <p>
 * This interface defines the business logic methods for adding, retrieving, updating,
 * deleting customers, and authenticating a customer.
 */

public interface CustomerService {
	
	public Customer addCustomer(Customer Customer);

	public Customer getCustomer(String username);

	public Customer updateCustomer(Customer Customer);

	public void deleteCustomer(String username);

	public ResponseEntity<?> authenticateCustomer(String username, String password);
}
