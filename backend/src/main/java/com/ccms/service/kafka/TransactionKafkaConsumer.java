package com.ccms.service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionKafkaConsumer {

    @KafkaListener(topics = "transaction-log-topic", groupId = "credit-card-consumer-group")
    public void listenTransactionLogs(String message) {

       	// Process customer message (e.g., persist to the database)
    	
        System.out.println("Received transaction logs: " + message);
    }
}
