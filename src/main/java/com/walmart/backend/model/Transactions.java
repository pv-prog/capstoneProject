package com.walmart.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "Transactions")
public class Transactions {
    @Id
    private String id;
    private String username;
    private List<CardId> creditcards;

    //@Override
    public String getId() {
        return id;
    }

    //@Override
    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<CardId> getCreditcards() {
        return creditcards;
    }

    public void setCreditcards(List<CardId> creditcards) {
        this.creditcards = creditcards;
    }

    public static class CardId {
    private int creditCardId;
    private List<TransactionsDetails> transactions;

    public int getCreditCardId() {
        return creditCardId;
    }

    public void setCreditCardId(int creditCardId) {
        this.creditCardId = creditCardId;
    }

        public List<TransactionsDetails> getTransactions() {
            return transactions;
        }

        public void setTransactions(List<TransactionsDetails> transactions) {
            this.transactions = transactions;
        }

        @Override
        public String toString() {
            return "CardId{" +
                    "creditCardId=" + creditCardId +
                    ", transactions=" + transactions +
                    '}';
        }
    }
public static class TransactionsDetails{
        private String transactionId;
        private Date transactionDate;
        private String transactionTime;
        private String transactionType;
        private Double transactionAmount;

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public Date getTransactionDate() {
            return transactionDate;
        }

        public void setTransactionDate(Date transactionDate) {
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

        public Double getTransactionAmount() {
            return transactionAmount;
        }

        public void setTransactionAmount(Double transactionAmount) {
            this.transactionAmount = transactionAmount;
        }

    @Override
    public String toString() {
        return "TransactionsDetails{" +
                "transactionId='" + transactionId + '\'' +
                ", transactionDate=" + transactionDate +
                ", transactionTime='" + transactionTime + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", transactionAmount=" + transactionAmount +
                '}';
    }
}

    @Override
    public String toString() {
        return "Transactions{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", creditcards=" + creditcards +
                '}';
    }
}
//    public static class MaxExpense {
//        private String creditCardNumber;
//        private Date transactionDate;
//        private Double transactionAmount;
//        public String getCreditCardNumber() {
//            return creditCardNumber;
//        }
//        public void setCreditCardNumber(String creditCardNumber) {
//            this.creditCardNumber = creditCardNumber;
//        }
//        public Date getTransactionDate() {
//            return transactionDate;
//        }
//        public void setTransactionDate(Date transactionDate) {
//            this.transactionDate = transactionDate;
//        }
//        public Double getTransactionAmount() {
//            return transactionAmount;
//        }
//        public void setTransactionAmount(Double transactionAmount) {
//            this.transactionAmount = transactionAmount;
//        }
//    }
