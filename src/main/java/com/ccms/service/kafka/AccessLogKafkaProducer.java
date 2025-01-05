package com.ccms.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccessLogKafkaProducer {

	private static final String ACCESSLOG_TOPIC = "access-logs"; // Kafka topic to send logs to

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

	public void sendLog(String accesslogs) {
		kafkaTemplate.send(ACCESSLOG_TOPIC, accesslogs);
	}
}