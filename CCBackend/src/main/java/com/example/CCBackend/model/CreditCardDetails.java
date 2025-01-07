package com.example.CCBackend.model;

public class CreditCardDetails {
    private int creditCardId;
    private String creditCardNumber;
    private int expiryMonth;
    private int expiryYear;
    private int cvv;
    private String wireTransactionVendor;
    private String status;
    private String expiry; // To capture yy/mm format

    // Getters and Setters

    public int getCreditCardId() {
        return creditCardId;
    }

    public void setCreditCardId(int creditCardId) {
        this.creditCardId = creditCardId;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(int expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public int getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(int expiryYear) {
        this.expiryYear = expiryYear;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public String getWireTransactionVendor() {
        return wireTransactionVendor;
    }

    public void setWireTransactionVendor(String wireTransactionVendor) {
        this.wireTransactionVendor = wireTransactionVendor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }
}
