package com.walmart.backend.repository;

import com.walmart.backend.model.Transactions;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transactions, String> {
    Transactions findByUsername(String username);

    //Transactions[] findAll(String username);
}
