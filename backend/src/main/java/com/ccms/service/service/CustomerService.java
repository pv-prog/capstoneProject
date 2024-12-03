package com.ccms.service.service;

import com.ccms.service.model.Customer;

public interface CustomerService {

	public Customer addCustomer(Customer Customer);

	public Customer getCustomer(String username);

	public Customer updateCustomer(Customer Customer);

	public void deleteCustomer(String username);

}
