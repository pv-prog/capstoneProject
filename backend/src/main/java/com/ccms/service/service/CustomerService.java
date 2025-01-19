package com.ccms.service.service;

import com.ccms.service.model.Customer;

/**
 * Service interface for managing customer operations.
 * <p>
 * This service provides methods for adding, retrieving, updating, and deleting customer records.
 * </p>
 */

public interface CustomerService {

	public Customer addCustomer(Customer Customer);

	public Customer getCustomer(String username);

	public Customer updateCustomer(Customer Customer);

	public void deleteCustomer(String username);

}
