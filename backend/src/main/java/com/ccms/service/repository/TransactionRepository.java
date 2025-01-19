package com.ccms.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ccms.service.model.Transaction;

/**
 * Repository interface for accessing {@link Transaction} data from MongoDB.
 * <p>
 * This repository extends {@link MongoRepository} to provide CRUD operations
 * and custom queries to find {@link Transaction} by username. It also supports
 * pagination for querying a user's transactions.
 * </p>
 */

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    /**
     * Finds a {@link Transaction} by the associated username.
     * <p>
     * This query retrieves the first {@link Transaction} document where the
     * username matches the provided argument. 
     * </p>
     *
     * @param username The username associated with the transactions.
     * @return The {@link Transaction} associated with the given username.
     */
	
	@Query("{username:'?0'}")
	Transaction findByUsername(String username);

    /**
     * Finds {@link Transaction} by username with pagination support.
     * <p>
     * This method allows you to fetch transactions by username, but with pagination
     * capabilities. The {@link Pageable} parameter allows you to specify the page number,
     * size, and sorting criteria.
     * </p>
     *
     * @param username The username associated with the transactions.
     * @param pageable Pageable object containing pagination and sorting information.
     * @return A {@link Page} of {@link Transaction} associated with the given username.
     */
	
	Page<Transaction> findByUsername(String username, Pageable pageable);

}
