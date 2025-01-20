package com.ccms.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionKafkaProducer {

    private static final String TRANSACTION_TOPIC = "transaction-log-topic"; // Kafka topic for transaction logs

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendTransactionLog(String transactionData) {
        kafkaTemplate.send(TRANSACTION_TOPIC, transactionData);
    }
}