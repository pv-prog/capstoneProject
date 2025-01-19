package com.ccms.service.kafka;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer service for processing customer logs from the Kafka topic.
 * <p>
 * This service listens to the "customer-log-topic" Kafka topic and processes
 * messages related to customer events or logs. The messages may represent
 * customer-related actions or data that should be processed, stored, or further
 * acted upon within the application.
 * </p>
 */

@Service
@EnableKafka
public class CustomerKafkaConsumer {

	/**
	 * Listens for messages from the "customer-log-topic" Kafka topic and processes
	 * them.
	 * <p>
	 * This method listens to customer-related logs or events that are sent to the
	 * Kafka topic and processes them accordingly. Typically, this could involve
	 * actions like persisting customer data or triggering further application logic
	 * based on the received log data.
	 * </p>
	 *
	 * @param message The message received from the Kafka topic, typically a log or
	 *                event related to a customer.
	 */

	@KafkaListener(topics = "customer-log-topic", groupId = "credit-card-consumer-group")
	public void listenCustomerLogs(String message) {

		// Log or process the customer-related message (e.g., saving to a database)

		System.out.println("Received customer logs: " + message);
	}
}
