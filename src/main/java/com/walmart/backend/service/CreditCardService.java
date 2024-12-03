package com.walmart.backend.service;

import com.walmart.backend.model.CreditCard;
import com.walmart.backend.model.Customer;
import com.walmart.backend.model.Transactions;
import com.walmart.backend.repository.CreditCardRepository;
import com.walmart.backend.repository.CustomerRepository;
import com.walmart.backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CreditCardService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private TransactionRepository transactionRepository;

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
    public CreditCard getCreditCard(String username) {
//        Customer customer = customerRepository.findByUsername(username);
//        if (customer == null) {
//            throw new RuntimeException("Customer not found");
//        }
        for (CreditCard creditCard : creditCardRepository.findAll()) {
            if (creditCard.getUsername().equals(username)) {
                return creditCard;
            }
        }
        return null;
    }
    public Transactions getCreditCardTransaction(String username) {
        CreditCard creditCard = creditCardRepository.findByUsername(username);
        if (creditCard == null) {
            throw new RuntimeException("CreditCard not found");
        }
        Transactions transactions = transactionRepository.findByUsername(username);
        if (transactions == null) {
            throw new RuntimeException("Transaction not found");
        }
        return null;

    }
}
