package com.ccms.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerKafkaProducer {
	
	
	private static final String CUSTOMER_TOPIC = "customer-log-topic"; // Kafka topic for customer logs

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendCustomerLog(String customerData) {
        kafkaTemplate.send(CUSTOMER_TOPIC, customerData);
    }

}
