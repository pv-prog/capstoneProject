package com.ccms.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka producer service for sending transaction logs to Kafka.
 * <p>
 * This component is responsible for producing transaction log messages and
 * sending them to the Kafka topic "transaction-log-topic". The transaction data
 * is expected to be a string, which is sent to Kafka via the KafkaTemplate.
 * </p>
 */

@Component
public class TransactionKafkaProducer {

	// Kafka topic to send transaction logs to

	private static final String TRANSACTION_TOPIC = "transaction-log-topic"; // Kafka topic for transaction logs

	// KafkaTemplate for sending messages to Kafka
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	/**
	 * Sends a transaction log message to the Kafka topic "transaction-log-topic".
	 * <p>
	 * This method sends the given transaction log message as a string to the Kafka
	 * topic specified in {@link #TRANSACTION_TOPIC}.
	 * </p>
	 *
	 * @param transactionData The transaction log data to be sent to Kafka. This is
	 *                        typically a JSON string or any other format.
	 */

	public void sendTransactionLog(String transactionData) {

		// Sending the transaction log data to Kafka

		kafkaTemplate.send(TRANSACTION_TOPIC, transactionData);
	}
}