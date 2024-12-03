package com.walmart.backend.repository;

import com.walmart.backend.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    Customer findByUsername(String username);
}
