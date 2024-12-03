package com.ccms.service.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ccms.service.model.CreditCard;
import com.ccms.service.model.CreditCard.CreditCardDetail;
import com.ccms.service.model.Customer;
import com.ccms.service.repository.CreditCardRepository;
import com.ccms.service.repository.CustomerRepository;
import com.ccms.service.service.CreditCardService;

@Service
public class CreditCardServiceimpl implements CreditCardService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	CreditCardRepository creditCardRepository;

	@Override
	public CreditCard getCreditcardforuser(String username) {

		return creditCardRepository.findByUsername1(username);
	}

	@Override
	public CreditCard addCreditCard(String username, CreditCardDetail creditCardDetail) {

		Customer customer = customerRepository.findByUsername(username);
		if (customer == null) {
			throw new RuntimeException("Customer not found");
		}

		CreditCard creditCard = creditCardRepository.findByUsername1(username);
		if (creditCard == null) {
			creditCard = new CreditCard();
			creditCard.setUsername(username);
			creditCard.setNameOnTheCard(customer.getName().getFirst() + " " + customer.getName().getLast());
			creditCard.setCreditcards(new ArrayList<>());
		}

		creditCard.getCreditcards().add(creditCardDetail);
		return creditCardRepository.save(creditCard);
	}

	@Override
	public void toggleCreditCardStatus(String username, int creditCardId) {

		CreditCard creditCard = creditCardRepository.findByUsername1(username);
		
		System.out.println(creditCard);
		if (creditCard != null) {
			for (CreditCard.CreditCardDetail card : creditCard.getCreditcards()) {
				if (card.getCreditCardId() == creditCardId) {
					card.setStatus(card.getStatus().equals("enabled") ? "disabled" : "enabled");
					break;
				}
			}
			creditCardRepository.save(creditCard);
		}
	}

	@Override
	public CreditCard getallCreditcardsforuser(String username) {

		System.out.println("Credit card service");

		return creditCardRepository.findByUsername(username);
	}

}
