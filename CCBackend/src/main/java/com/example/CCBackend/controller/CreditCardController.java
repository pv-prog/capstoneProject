package com.example.CCBackend.controller;

import com.example.CCBackend.model.CreditCardDetails;
import com.example.CCBackend.service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/g/api/customer/creditcard")
public class CreditCardController {

    @Autowired
    private CreditCardService service;

    @PostMapping("/")
    public ResponseEntity<String> addCreditCard(@RequestParam String username, @RequestBody CreditCardDetails cardDetails) {
        String response = service.addCreditCard(username, cardDetails);

        if (response.equals("Card added successfully")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}

