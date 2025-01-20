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
		
        // Check if the access log contains '/actuator/prometheus' and skip those logs
		
        if (accesslogs.contains("/actuator/prometheus")) {
            return; // Skip this log
        }

        // Send the valid log to Kafka
        
		kafkaTemplate.send(ACCESSLOG_TOPIC, accesslogs);
	}
}