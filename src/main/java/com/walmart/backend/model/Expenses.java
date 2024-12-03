package com.walmart.backend.model;

import java.util.Date;

public class Expenses {
    private String creditCardNumber;
    private Double transactionAmount;
    private Date transactionDate;

    public Expenses() {
    }

    public Expenses(Double maxBalance, String cardCr, String trTime) {
    }

    public Expenses(String creditCardNumber, Date transactionDate, Double transactionAmount) {
        this.creditCardNumber = creditCardNumber;
        this.transactionDate = transactionDate;
        this.transactionAmount = transactionAmount;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "Expenses{" +
                "creditCardNumber='" + creditCardNumber + '\'' +
                ", transactionAmount=" + transactionAmount + '\'' +
                ", transactionDate=" + transactionDate +
                '}';
    }
}
