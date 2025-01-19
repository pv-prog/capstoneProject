package com.ccms.service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ccms.service.model.Customer;

/**
 * Repository interface for accessing {@link Customer} data from MongoDB.
 * <p>
 * This repository extends {@link MongoRepository} to provide CRUD operations
 * and a custom query for finding a {@link Customer} by their username.
 * </p>
 */

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {

	/**
	 * Finds a {@link Customer} by their associated username.
	 * <p>
	 * This query retrieves the {@link Customer} document based on the provided
	 * username.
	 * </p>
	 *
	 * @param username The username to search for.
	 * @return The {@link Customer} associated with the given username.
	 */

	@Query("{username:'?0'}")
	Customer findByUsername(String username);
}
