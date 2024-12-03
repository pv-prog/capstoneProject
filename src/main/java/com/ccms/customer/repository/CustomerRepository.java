package com.ccms.customer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ccms.customer.model.Customer;



public interface CustomerRepository extends MongoRepository<Customer, String> {

	@Query("{username:'?0'}")
	Customer findByUsername(String username);
	
}
