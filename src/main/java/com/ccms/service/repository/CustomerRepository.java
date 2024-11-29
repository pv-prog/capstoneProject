package com.ccms.service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.ccms.service.model.Customer;


public interface CustomerRepository extends MongoRepository<Customer, String> {

	@Query("{username:'?0'}")
	Customer findByUsername(String username);
}
