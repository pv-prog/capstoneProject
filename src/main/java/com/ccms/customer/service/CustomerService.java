package com.ccms.customer.service;

import com.ccms.customer.model.Customer;

public interface CustomerService {
	
	public Customer addCustomer(Customer Customer);

	public Customer getCustomer(String username);

	public Customer updateCustomer(Customer Customer);

	public void deleteCustomer(String username);

	public String authenticateCustomer(String username, String password);
}
