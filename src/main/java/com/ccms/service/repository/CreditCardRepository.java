package com.ccms.service.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.ccms.service.model.CreditCard;

public interface CreditCardRepository extends MongoRepository<CreditCard, String> {

	@Query("{username:'?0'}")
	CreditCard findByUsername1(String username);
	
	@Query(value = "{ 'username': ?0 }", fields = "{ 'creditcards': 1, '_id': 0 }")
	CreditCard findByUsername(String username);
	
}
