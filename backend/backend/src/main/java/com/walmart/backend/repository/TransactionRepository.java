package com.walmart.backend.repository;

import com.walmart.backend.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    Transaction findByUsername(String username);
}
