package com.walmart.backend.service;

import com.walmart.backend.model.CreditCard;
import com.walmart.backend.model.Expenses;
import com.walmart.backend.model.Transactions;
import com.walmart.backend.repository.CreditCardRepository;
import com.walmart.backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.*;
import java.util.*;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CreditCardRepository creditCardRepository;

    public Transactions cardTransactions(Transactions transaction) {
        CreditCard creditCard = creditCardRepository.findByUsername(transaction.getUsername());
        for( CreditCard.CreditCardDetail card : creditCard.getCreditcards()) {
            if (card.getCreditCardId() == transaction.getCreditcards().listIterator().next().getCreditCardId()) {
                if(card.getStatus().equals("enabled"))
                {
                return transactionRepository.save(transaction);
            }
            else{
                throw new RuntimeException("Credit Card Status is disabled");
                }
            }
        }
        return transaction;
    }
    public Transactions transactionsList(String username) {
        for (Transactions transaction : transactionRepository.findAll()) {
            if (transaction.getUsername().equals(username)) {
                return transaction;
            }
        }
        return null;
    }
    public List<Expenses> getLastMonthTransactionsMax(String username) throws ParseException {
        Map<String, Expenses> expenses = new HashMap<>();
        Map<String, Expenses> maxExpenses = new HashMap<>();
        for (CreditCard creditCard : creditCardRepository.findAll())
        {
            if(creditCard.getUsername().equals(username))
            {
                Transactions transaction = transactionRepository.findByUsername(username);
                if (transaction == null)
                {
                    return null;
                }
                List<Expenses> exp = new ArrayList<>();
                int j = 0;
                int i = 0;
                    for (Transactions.CardId tr : transaction.getCreditcards())
                    {
                        String cdrNumber = "";
                        for (CreditCard.CreditCardDetail ex : creditCard.getCreditcards())
                        {
                            if (ex.getCreditCardId() == tr.getCreditCardId())
                            {
                                cdrNumber = ex.getCreditCardNumber();
                            }
                        }
                        for (Transactions.TransactionsDetails tr1 : tr.getTransactions())
                        {
                            Expenses e1 = new Expenses();
                            e1.setCreditCardNumber(cdrNumber);
                            e1.setTransactionDate(tr1.getTransactionDate());
                            e1.setTransactionAmount(tr1.getTransactionAmount());
                            expenses.putIfAbsent(String.valueOf(j), e1);
                            ++j;
                            e1 = null;
                            System.gc();
                        }
                        j = 0;
                        Expenses x = new Expenses();
                        Double maxBalance=0.0;
                        LocalDate now = LocalDate.now();
                        LocalDate earlier = now.minusMonths(1);
                        for(Map.Entry<String, Expenses> entry : expenses.entrySet())
                        {
                            if (entry.getValue().getTransactionAmount() > maxBalance)
                            {
                                x.setCreditCardNumber(entry.getValue().getCreditCardNumber());
                                LocalDate trDate = entry.getValue().getTransactionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                if (trDate.compareTo(earlier) >= 0 && (trDate.compareTo(now) <= 0))
                                {
                                    maxBalance = entry.getValue().getTransactionAmount();
                                    x.setTransactionDate(entry.getValue().getTransactionDate());
                                    x.setTransactionAmount(entry.getValue().getTransactionAmount());
                                }
                            }
                        }
                        maxExpenses.putIfAbsent(String.valueOf(i), x);
                        System.out.println("Transaction Details for credit Card Number : " + x.getCreditCardNumber()+ " " +expenses);
                        System.out.println("Max Transaction Amount for Last Month: " + maxExpenses.getOrDefault(String.valueOf(i), x));
                        System.out.println("========================================================================================");
                        x = null;
                        ++i;
                        expenses.clear();
                        System.gc();
                    }
                System.out.println("Last Month Maximum Expenses of all Credit Cards");
                for (Map.Entry<String, Expenses> entry : maxExpenses.entrySet())
                {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                    exp.add(entry.getValue());
                }
                return exp;
            }
        }
        return null;
    }
}