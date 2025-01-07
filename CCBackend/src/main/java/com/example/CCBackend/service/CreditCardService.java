package com.example.CCBackend.service;

import com.example.CCBackend.model.CreditCard;
import com.example.CCBackend.model.CreditCardDetails;
import com.example.CCBackend.repository.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreditCardService {

    @Autowired
    private CreditCardRepository repository;

    public String addCreditCard(String username, CreditCardDetails cardDetails) {
        try {
            // Parse expiry in yy/mm format from cardDetails
            String[] expParts = cardDetails.getExpiry().split("/");
            if (expParts.length != 2) {
                throw new IllegalArgumentException("Invalid expiry format");
            }

            int expiryYear = Integer.parseInt("20" + expParts[0]); // Convert yy to yyyy
            int expiryMonth = Integer.parseInt(expParts[1]);

            if (expiryMonth < 1 || expiryMonth > 12) {
                throw new IllegalArgumentException("Invalid month in expiry");
            }

            cardDetails.setExpiryYear(expiryYear);
            cardDetails.setExpiryMonth(expiryMonth);

            CreditCard creditCard = repository.findByUsername(username);
            System.out.println("Searching for user: " + username);
//            System.out.println("User found: " + creditCard.getUsername());

            if (creditCard.getUsername() == null) {
                return "User not found";
            } else {
                // Validate if card already exists
                boolean cardExists = creditCard.getCreditcards().stream()
                        .anyMatch(card -> card.getCreditCardNumber().equals(cardDetails.getCreditCardNumber()));

                if (cardExists) {
                    return "Card already exists";
                }

                int newCardId = creditCard.getCreditcards().size() + 1;
                cardDetails.setCreditCardId(newCardId);
                cardDetails.setStatus("enabled"); // Default status
                creditCard.getCreditcards().add(cardDetails);
                repository.save(creditCard);
                return "Card added successfully";
            }
        } catch (NumberFormatException e) {
            return "Invalid expiry format: non-numeric values found";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "An unexpected error occurred";
        }
    }
}
