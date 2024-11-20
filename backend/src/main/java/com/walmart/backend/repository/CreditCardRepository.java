package com.walmart.backend.repository;

import com.walmart.backend.model.CreditCard;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CreditCardRepository extends MongoRepository<CreditCard, String> {
    CreditCard findByUsername(String username);
}
