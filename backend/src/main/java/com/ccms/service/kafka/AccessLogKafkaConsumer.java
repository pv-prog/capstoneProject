package com.ccms.service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AccessLogKafkaConsumer {

    @KafkaListener(topics = "access-logs", groupId = "credit-card-consumer-group")
    public void listenTransactionLogs(String message) {
    	
        System.out.println("Received access logs: " + message);
    }
}


