package com.ccms.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

/**
 * The main entry point for the CCMS Customer Application, responsible for bootstrapping the Spring Boot application.
 * <p>
 * This class is annotated with {@link SpringBootApplication} to mark it as a Spring Boot application, and it also
 * includes component scanning for the base package {@code com.ccms.customer}. Additionally, it enables the discovery
 * of this service in a microservices architecture using {@link EnableDiscoveryClient}.
 * </p>
 * <p>
 * The application also includes a {@link RestTemplate} bean that is {@link LoadBalanced}, enabling it to make REST 
 * calls to other services in a microservices environment with load balancing.
 * </p>
 * 
 * @since 1.0
 */

@SpringBootApplication
@ComponentScan(basePackages = "com.ccms.customer")
@EnableDiscoveryClient
public class CcmsCustomerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CcmsCustomerApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate()
	{
		return new RestTemplate();
	}

}
