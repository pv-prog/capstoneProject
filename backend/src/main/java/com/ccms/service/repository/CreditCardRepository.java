package com.ccms.service.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ccms.service.model.CreditCard;

/**
 * Repository interface for accessing {@link CreditCard} data from MongoDB.
 * <p>
 * This repository extends {@link MongoRepository} to provide CRUD operations
 * and custom queries for {@link CreditCard} entities.
 * </p>
 */

@Repository
public interface CreditCardRepository extends MongoRepository<CreditCard, String> {

	
    /**
     * Finds a {@link CreditCard} by its associated username.
     * <p>
     * This query retrieves the entire credit card document based on the provided username.
     * </p>
     *
     * @param username The username to search for.
     * @return The {@link CreditCard} associated with the given username.
     */
	
	@Query("{username:'?0'}")
	CreditCard findByUsername1(String username);
	
	
    /**
     * Finds a {@link CreditCard} by its associated username, retrieving only the credit cards.
     * <p>
     * This query retrieves the `creditcards` field from the document, excluding the `_id` field.
     * </p>
     *
     * @param username The username to search for.
     * @return A {@link CreditCard} with only the credit card details (excluding the `_id` field).
     */
	
	@Query(value = "{ 'username': ?0 }", fields = "{ 'creditcards': 1, '_id': 0 }")
	CreditCard findByUsername(String username);

}
