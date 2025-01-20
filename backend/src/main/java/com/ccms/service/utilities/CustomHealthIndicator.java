package com.ccms.service.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String HEALTH_METRIC_NAME = "spring_boot_health_status";

    @Override
    public Health health() {
        boolean systemIsHealthy = checkSystemHealth();

        // Update health status in the MeterRegistry using a tag for "status"
        String healthStatus = systemIsHealthy ? "UP" : "DOWN";
        
        // Set the status using a tag (instead of a simple gauge with 1.0 or 0.0)
        meterRegistry.gauge(HEALTH_METRIC_NAME, Tags.of("status", healthStatus), systemIsHealthy ? 1 : 0);

        if (systemIsHealthy) {
            return Health.up().withDetail("status", "UP").build();
        } else {
            return Health.down().withDetail("status", "DOWN").build();
        }
    }

    private boolean checkSystemHealth() {
        try {
            // Example: check if the MongoDB connection is up
            mongoTemplate.executeCommand("{ ping: 1 }");
            return true; // MongoDB is reachable
        } catch (Exception e) {
            return false; // MongoDB is not reachable
        }
    }
}
