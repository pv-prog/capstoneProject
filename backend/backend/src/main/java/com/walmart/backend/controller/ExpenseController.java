package com.walmart.backend.controller;

import com.walmart.backend.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/creditcards/expenses")
    public ResponseEntity<List<ExpenseService.ExpenseReport>> getExpenses(@RequestParam String username) {
        try {
            List<ExpenseService.ExpenseReport> expenses = expenseService.getMonthlyExpenses(username);
            return ResponseEntity.ok(expenses);
        } catch (ParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
