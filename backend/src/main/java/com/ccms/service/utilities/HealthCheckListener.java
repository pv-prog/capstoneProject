package com.ccms.service.utilities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HealthCheckListener implements ApplicationListener<ApplicationReadyEvent> {
	
    @Value("${server.port}")
    private int port;


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        checkHealthOnStartup();
    }

    public void checkHealthOnStartup() {
    	
        String healthCheckUrl = "http://localhost:" + port + "/actuator/health";
        

        try {
            RestTemplate restTemplate = new RestTemplate();
            String healthStatus = restTemplate.getForObject(healthCheckUrl, String.class);
            System.out.println("Health check response on startup: " + healthStatus);
        } catch (Exception e) {
            System.err.println("Health check failed: " + e.getMessage());
        }
    }
}
