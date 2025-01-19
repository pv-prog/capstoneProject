package com.ccms.service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer service for processing credit card logs.
 * <p>
 * This service listens to the Kafka topic "creditcard-log-topic" and processes the incoming messages.
 * The messages could represent events related to credit card transactions or actions performed on a credit card.
 * </p>
 */

@Service
public class CreditCardKafkaConsumer {

	 /**
     * Kafka listener that listens for messages on the "creditcard-log-topic" topic.
     * The consumer group is "credit-card-consumer-group".
     * <p>
     * The messages from this topic are typically related to credit card transactions, 
     * customer actions, or any events involving credit cards.
     * </p>
     * 
     * @param message The message received from the Kafka topic. This is typically a log or event 
     *                that needs to be processed (e.g., saved to a database or further analyzed).
     */
	
    @KafkaListener(topics = "creditcard-log-topic", groupId = "credit-card-consumer-group")
    public void listenCreditCardLogs(String message) {
       
        // Log or process the incoming credit card log message
        // For example, you could persist this data to a database
    	
        System.out.println("Received creditcard logs: " + message);
    }
}
