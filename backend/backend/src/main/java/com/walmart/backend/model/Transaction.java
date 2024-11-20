package com.walmart.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Transactions")
public class Transaction {
    @Id
    private String id;
    private String username;
    private List<CreditCardTransaction> creditcards;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<CreditCardTransaction> getCreditcards() {
        return creditcards;
    }

    public void setCreditcards(List<CreditCardTransaction> creditcards) {
        this.creditcards = creditcards;
    }

    public static class CreditCardTransaction {
        private int creditCardId;
        private List<TransactionDetail> transactions;

        public int getCreditCardId() {
            return creditCardId;
        }

        public void setCreditCardId(int creditCardId) {
            this.creditCardId = creditCardId;
        }

        public List<TransactionDetail> getTransactions() {
            return transactions;
        }

        public void setTransactions(List<TransactionDetail> transactions) {
            this.transactions = transactions;
        }
    }

    public static class TransactionDetail {
        private long transactionId;
        private String transactionDate;
        private String transactionTime;
        private String transactionType; // cr or db
        private String transactionAmount;

        public long getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(long transactionId) {
            this.transactionId = transactionId;
        }

        public String getTransactionDate() {
            return transactionDate;
        }

        public void setTransactionDate(String transactionDate) {
            this.transactionDate = transactionDate;
        }

        public String getTransactionTime() {
            return transactionTime;
        }

        public void setTransactionTime(String transactionTime) {
            this.transactionTime = transactionTime;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }

        public String getTransactionAmount() {
            return transactionAmount;
        }

        public void setTransactionAmount(String transactionAmount) {
            this.transactionAmount = transactionAmount;
        }
    }
}
