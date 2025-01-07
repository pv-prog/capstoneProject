package com.example.CCBackend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "CreditCard")
public class CreditCard {

    @Id
    private String id;
    private String username;
    private String nameOnTheCard;
    private List<CreditCardDetails> creditcards;

    // Getters and Setters


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

    public String getNameOnTheCard() {
        return nameOnTheCard;
    }

    public void setNameOnTheCard(String nameOnTheCard) {
        this.nameOnTheCard = nameOnTheCard;
    }

    public List<CreditCardDetails> getCreditcards() {
        return creditcards;
    }

    public void setCreditcards(List<CreditCardDetails> creditcards) {
        this.creditcards = creditcards;
    }
}
