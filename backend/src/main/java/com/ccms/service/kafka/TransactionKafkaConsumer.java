package com.ccms.service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer service for processing transaction logs from Kafka.
 * <p>
 * This service listens to the Kafka topic "transaction-log-topic" and processes
 * incoming transaction log messages. The messages are assumed to be strings and
 * can be further processed, such as persisting to a database or triggering
 * further actions in the system.
 * </p>
 */

@Service
public class TransactionKafkaConsumer {

	/**
	 * Listens for incoming messages from the "transaction-log-topic" Kafka topic.
	 * <p>
	 * This method is triggered whenever a new message is sent to the Kafka topic
	 * "transaction-log-topic". The message is processed in this method, and in this
	 * case, it is printed to the console. In a real-world scenario, the message
	 * could be persisted to a database or trigger other business logic.
	 * </p>
	 *
	 * @param message The transaction log message received from the Kafka topic.
	 */

	@KafkaListener(topics = "transaction-log-topic", groupId = "credit-card-consumer-group")
	public void listenTransactionLogs(String message) {

		// Process the incoming transaction log message

		System.out.println("Received transaction logs: " + message);
	}
}
