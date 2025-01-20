package com.ccms.service.utilities;

import java.time.Instant;
import java.util.Map;
import org.json.JSONObject;

import com.ccms.service.kafka.CreditCardKafkaProducer;

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


