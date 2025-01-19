package com.ccms.service.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

/**
 * Custom health indicator to monitor the health status of the application.
 * <p>
 * This class implements {@link HealthIndicator} and provides a custom implementation of the health check.
 * It checks the health status by performing a simple MongoDB ping using {@link MongoTemplate}. Based on the 
 * result of the health check, it updates a custom health metric in the {@link MeterRegistry} to indicate the 
 * application's overall health status.
 * </p>
 * <p>
 * The health status is tagged as either "UP" or "DOWN" and recorded in the MeterRegistry, allowing it to be 
 * queried by monitoring systems. The custom health indicator will return a `Health` object indicating the health 
 * status, either up or down, with a corresponding detail of "status" as "UP" or "DOWN".
 * </p>
 * 
 * @see HealthIndicator
 * @see MeterRegistry
 * @see MongoTemplate
 */

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
