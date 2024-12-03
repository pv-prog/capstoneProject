package com.ccms.customer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ccms.customer.model.Customer;
import com.ccms.customer.repository.CustomerRepository;
import com.ccms.customer.service.CustomerService;
import com.ccms.customer.utilities.JwtUtil;

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
	public String authenticateCustomer(String username, String password) {

		Customer customer = customerRepository.findByUsername(username);

		if (customer != null && passwordEncoder.matches(password, customer.getPassword())) {

			String token = jwtUtil.generateToken(username); // Generate token if authentication is successful
			
		     System.out.println("Token!!!" +token);
			
			return token;
		}

		return null; // Return null if authentication fails
	}

}
