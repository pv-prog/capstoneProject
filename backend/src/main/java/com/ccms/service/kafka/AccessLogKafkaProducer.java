package com.ccms.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for producing access logs and sending them to a Kafka topic.
 * <p>
 * This class uses the {@link KafkaTemplate} to send access logs to a Kafka topic named "access-logs".
 * It also contains a simple filtering mechanism to exclude logs containing the "/actuator/prometheus" path.
 * </p>
 */

@Service
public class AccessLogKafkaProducer {
	
	
	/**
     * The Kafka topic to which the access logs are sent.
     * The logs are sent to this topic for further processing (e.g., consumption by other services).
     */

	private static final String ACCESSLOG_TOPIC = "access-logs"; // Kafka topic to send logs to

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    
    /**
     * Sends the provided access log to the Kafka topic, excluding logs related to Prometheus.
     * <p>
     * The method checks if the log contains the string "/actuator/prometheus", which is typically used
     * for Prometheus metrics endpoints. If the log contains this string, it will be skipped and not sent
     * to Kafka.
     * </p>
     * 
     * @param accesslogs The access log message to be sent. This log could contain information about 
     *                   user actions, system events, or any other access-related data.
     */

	public void sendLog(String accesslogs) {
		
       
		 // Skip logs containing "/actuator/prometheus"
		
        if (accesslogs.contains("/actuator/prometheus")) {
            return; // Skip this log
        }

        // Send the valid log to Kafka
        
		kafkaTemplate.send(ACCESSLOG_TOPIC, accesslogs);
	}
}