package com.walmart.backend.service;

import com.walmart.backend.model.CreditCard;
import com.walmart.backend.model.Customer;
import com.walmart.backend.repository.CreditCardRepository;
import com.walmart.backend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CreditCardService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    public CreditCard addCreditCard(String username, CreditCard.CreditCardDetail creditCardDetail) {
        Customer customer = customerRepository.findByUsername(username);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        CreditCard creditCard = creditCardRepository.findByUsername(username);
        if (creditCard == null) {
            creditCard = new CreditCard();
            creditCard.setUsername(username);
            creditCard.setNameOnTheCard(customer.getName().getFirst() + " " + customer.getName().getLast());
            creditCard.setCreditcards(new ArrayList<>());
        }

        creditCard.getCreditcards().add(creditCardDetail);
        return creditCardRepository.save(creditCard);
    }
}

