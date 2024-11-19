package com.walmart.backend.controller;

import com.walmart.backend.model.CreditCard;
import com.walmart.backend.service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/creditcard")
public class CreditCardController {
    @Autowired
    private CreditCardService creditCardService;

    @PostMapping("/{username}")
    public ResponseEntity<CreditCard> addCreditCard(@PathVariable String username, @RequestBody CreditCard.CreditCardDetail creditCardDetail) {
        CreditCard updatedCreditCard = creditCardService.addCreditCard(username, creditCardDetail);
        return ResponseEntity.ok(updatedCreditCard);
    }
}
