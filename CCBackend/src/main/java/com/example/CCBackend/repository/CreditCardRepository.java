package com.example.CCBackend.repository;

import com.example.CCBackend.model.CreditCard;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CreditCardRepository extends MongoRepository<CreditCard, String> {
    CreditCard findByUsername(String username);
}