package com.ccms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ccms.service.model.CreditCard;
import com.ccms.service.model.Customer;
import com.ccms.service.model.Transaction;
import com.ccms.service.service.CreditCardService;
import com.ccms.service.service.CustomerService;
import com.ccms.service.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Customer Controller", description = "Controller for managing Customers")
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

	@Autowired
	CustomerService customerService;

//	@Autowired
//	CreditCardService creditCardService;
//
//	@Autowired
//	TransactionService transactionService;

	@Operation(summary = "Get Customer Profile", description = "Show the profile details for the specified customer")
	@GetMapping("/{username}")
	public Customer getCustomer(@PathVariable("username") String username) {

		System.out.println(customerService.getCustomer(username));

		return customerService.getCustomer(username);

	}

////Rest Template for Microservices
//	@Autowired
//	
//	RestTemplate restTemplate;
//	
//	@GetMapping("/order")
////	@CircuitBreaker(name="ORDER_SERVICE" ,fallbackMethod="orderFallback")
//	public ResponseEntity<String> getOrder()
//	{
//		String response=restTemplate.getForObject("http://localhost:9009/item",String.class);
//	//String response=restTemplate.getForObject("http://ITEM-SERVICE/item",String.class);
//		return new ResponseEntity<String>(response,HttpStatus.OK);
//		
//	}

}