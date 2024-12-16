package com.ccms.service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.http.ResponseEntity;

import com.ccms.service.model.Transaction;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

	@Query("{username:'?0'}")
	Transaction findByUsername(String username);

}
