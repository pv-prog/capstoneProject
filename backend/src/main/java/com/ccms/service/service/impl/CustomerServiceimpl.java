package com.ccms.service.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ccms.service.model.Customer;
import com.ccms.service.repository.CustomerRepository;
import com.ccms.service.service.CustomerService;

@Service
public class CustomerServiceimpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository;

	@Override
	public Customer addCustomer(Customer Customer) {
		// TODO Auto-generated method stub
		return null;
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

}
