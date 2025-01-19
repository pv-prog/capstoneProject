package com.ccms.customer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ccms.customer.model.Customer;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Customer} entities.
 * <p>
 * This interface extends {@link MongoRepository}, providing CRUD operations for the Customer entity.
 * It also defines custom queries using {@link Query} annotations.
 */

public interface CustomerRepository extends MongoRepository<Customer, String> {

	@Query("{username:'?0'}")
	Customer findByUsername(String username);
	
	@Query("{username:'?0'}")
	Optional<CustomerProjection> findUsernameAndPasswordByUsername(String username);
}
