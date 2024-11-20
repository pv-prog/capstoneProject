package com.walmart.backend.service;

import com.walmart.backend.model.Transaction;
import com.walmart.backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ExpenseService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<ExpenseReport> getMonthlyExpenses(String username) throws ParseException {
        Transaction transaction = transactionRepository.findByUsername(username);
        if (transaction == null) {
            return Collections.emptyList();
        }

        Map<String, List<ExpenseDetail>> expenseMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        for (Transaction.CreditCardTransaction creditCard : transaction.getCreditcards()) {
            for (Transaction.TransactionDetail detail : creditCard.getTransactions()) {
                Date date = dateFormat.parse(detail.getTransactionDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                String month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH).toUpperCase();
                String type = detail.getTransactionType();

                // Initialize the expense list if it does not exist
                expenseMap.putIfAbsent(creditCard.getCreditCardId() + "", new ArrayList<>());
                expenseMap.get(creditCard.getCreditCardId() + "").add(new ExpenseDetail(month, Double.parseDouble(detail.getTransactionAmount()), type));
            }
        }

        List<ExpenseReport> expenseReports = new ArrayList<>();
        for (Map.Entry<String, List<ExpenseDetail>> entry : expenseMap.entrySet()) {
            expenseReports.add(new ExpenseReport(entry.getKey(), entry.getValue()));
        }

        return expenseReports;
    }

    public static class ExpenseReport {
        private String creditCard;
        private List<ExpenseDetail> expenses;

        public ExpenseReport(String creditCard, List<ExpenseDetail> expenses) {
            this.creditCard = creditCard;
            this.expenses = expenses;
        }

        public String getCreditCard() {
            return creditCard;
        }

        public void setCreditCard(String creditCard) {
            this.creditCard = creditCard;
        }

        public List<ExpenseDetail> getExpenses() {
            return expenses;
        }

        public void setExpenses(List<ExpenseDetail> expenses) {
            this.expenses = expenses;
        }
    }

    public static class ExpenseDetail {
        private String month;
        private double amount;
        private String type;

        public ExpenseDetail(String month, double amount, String type) {
            this.month = month;
            this.amount = amount;
            this.type = type;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
