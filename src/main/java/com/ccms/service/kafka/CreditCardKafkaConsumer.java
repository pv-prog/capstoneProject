package com.ccms.service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CreditCardKafkaConsumer {

    @KafkaListener(topics = "creditcard-log-topic", groupId = "credit-card-consumer-group")
    public void listenCreditCardLogs(String message) {
       
       	// Process customer message (e.g., persist to the database)
    	
        System.out.println("Received creditcard logs: " + message);
    }
}
