package com.ccms.service.utilities;

import java.time.Instant;
import java.util.Map;
import org.json.JSONObject;

import com.ccms.service.kafka.CreditCardKafkaProducer;


/**
 * Utility class for logging credit card related events and sending logs to Kafka.
 * <p>
 * This class contains a static method to create a structured log and send it to Kafka. It provides 
 * a way to log information related to credit card actions (such as success or failure), including 
 * the username, credit card ID, and other relevant details.
 * </p>
 * 
 * <p>
 * The log is prepared in the form of a JSON object, which includes the customer's username, credit 
 * card ID, status of the action, message, and a timestamp. After the log is prepared, it is sent to 
 * a Kafka topic through the provided {@link CreditCardKafkaProducer}.
 * </p>
 * 
 * @see CreditCardKafkaProducer
 */

public class CreditCardLogUtil {
	
	
	  // Common method for creating and sending the log
	
    public static void logCreditCard(Map<String, Object> jsonLogMap, 
                                            String status, 
                                            String message, 
                                            String username, 
                                            int creditCardId,
                                            CreditCardKafkaProducer creditCardKafkaProducer) {
        
        // Prepare the log data
        jsonLogMap.put("customer_name", username);
        jsonLogMap.put("credit_card_id", creditCardId);
        jsonLogMap.put("message", message);
        jsonLogMap.put("status", status);
        jsonLogMap.put("timestamp", Instant.now().toString());

        // Convert the map to a JSONObject and send it to Kafka
        
        creditCardKafkaProducer.sendCreditCardLog(new JSONObject(jsonLogMap).toString());
    }
}


