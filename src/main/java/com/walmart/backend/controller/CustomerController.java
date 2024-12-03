package com.walmart.backend.controller;

import com.walmart.backend.model.Customer;
import com.walmart.backend.repository.CustomerRepository;
import com.walmart.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/addCustomer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @PostMapping("/new")
    ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) {
        Customer customer1 = customerService.addCustomer(customer);
        return ResponseEntity.ok(customer1);
    }
}
