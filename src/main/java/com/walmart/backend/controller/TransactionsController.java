package com.walmart.backend.controller;

import com.walmart.backend.model.Expenses;
import com.walmart.backend.model.Transactions;
import com.walmart.backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionsController {
    @Autowired
    private TransactionService transactionService;
    @PostMapping("/addTransaction")
    ResponseEntity<Transactions> addTransaction(@RequestBody Transactions transactions) {
        Transactions transactions1 = transactionService.cardTransactions(transactions);
        return ResponseEntity.ok(transactions1);
    }
    @GetMapping("/{username}/getTransactions")
    ResponseEntity<Transactions> getAllTransactions(@PathVariable String username) {
        Transactions transactions = transactionService.transactionsList(username);
        return ResponseEntity.ok(transactions);
    }
    @GetMapping("/{username}/creditcards/lastmonth/max")
    ResponseEntity<List<Expenses>> getLastMonthTransactionsMax(@PathVariable String username) {
        try {
            List<Expenses> expenses = transactionService.getLastMonthTransactionsMax(username);
            return ResponseEntity.ok(expenses);
        }
        catch (ParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }
//@GetMapping("/{username}/creditcards/lastmonth/max")
//public ResponseEntity<Double> getCreditCardTransaction(@PathVariable String username) {
//    Double transactions = transactionService.expenses(username);
//    return ResponseEntity.ok(transactions);
}
