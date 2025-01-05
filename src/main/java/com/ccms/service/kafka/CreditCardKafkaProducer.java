package com.ccms.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CreditCardKafkaProducer {
	
	   private static final String CREDITCARD_TOPIC = "creditcard-log-topic"; // Kafka topic for Credit Card logs

	    @Autowired
	    private KafkaTemplate<String, String> kafkaTemplate;

	    public void sendCreditCardLog(String creditcardData) {
	        kafkaTemplate.send(CREDITCARD_TOPIC, creditcardData);
	    }

}
