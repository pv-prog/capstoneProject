package com.ccms.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka producer service for sending credit card logs to the Kafka topic.
 * <p>
 * This service sends credit card related log data to a specified Kafka topic.
 * The Kafka topic used for sending logs is "creditcard-log-topic". The data
 * sent could represent events or logs related to credit card transactions or
 * actions.
 * </p>
 */

@Component
public class CreditCardKafkaProducer {

	// Kafka topic for Credit Card logs

	private static final String CREDITCARD_TOPIC = "creditcard-log-topic"; // Kafka topic for Credit Card logs

	// KafkaTemplate used to send messages to Kafka

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	/**
	 * Sends credit card log data to the Kafka topic.
	 * <p>
	 * This method is used to send the provided credit card log message to the
	 * "creditcard-log-topic" Kafka topic.
	 * </p>
	 *
	 * @param creditcardData The log or event data related to a credit card that
	 *                       will be sent to Kafka. This could be a string
	 *                       representation of the event or a log entry.
	 */

	public void sendCreditCardLog(String creditcardData) {

		// Sending the credit card log message to the Kafka topic

		kafkaTemplate.send(CREDITCARD_TOPIC, creditcardData);
	}

}
