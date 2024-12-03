package com.walmart.backend.service;

import com.walmart.backend.model.Customer;
import com.walmart.backend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    public Customer addCustomer(Customer customer) {
        if(customerRepository.findByUsername(customer.getUsername()) != null) {
            throw new RuntimeException("Customer already exists");
        }
        //Customer savedCustomer = customerRepository.save(customer);
        else {
            Customer savedCustomer = customerRepository.save(customer);
            return savedCustomer;
        }
    }
}
