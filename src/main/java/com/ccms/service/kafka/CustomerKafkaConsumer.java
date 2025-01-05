package com.ccms.service.kafka;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@EnableKafka
public class CustomerKafkaConsumer {

    @KafkaListener(topics = "customer-log-topic", groupId = "credit-card-consumer-group")
    public void listenCustomerLogs(String message) {
       
    	// Process customer message (e.g., persist to the database)
    	
        System.out.println("Received customer logs: " + message);
    }
}
