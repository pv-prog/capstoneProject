package com.ccms.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka producer service for sending customer logs to a Kafka topic.
 * <p>
 * This service is responsible for sending messages related to customer actions
 * or logs to the Kafka topic called "customer-log-topic". It uses KafkaTemplate
 * to produce the messages.
 * </p>
 */

@Component
public class CustomerKafkaProducer {

	// Kafka topic to send customer logs
	private static final String CUSTOMER_TOPIC = "customer-log-topic";

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	/**
	 * Sends a customer-related log or data to the Kafka topic.
	 * <p>
	 * This method sends customer data or logs (in the form of a string) to the
	 * specified Kafka topic. It utilizes KafkaTemplate to produce the message to
	 * Kafka.
	 * </p>
	 *
	 * @param customerData The customer log or event data to be sent to Kafka.
	 */

	public void sendCustomerLog(String customerData) {
		// Send the customer log to the Kafka topic
		kafkaTemplate.send(CUSTOMER_TOPIC, customerData);
	}

}
