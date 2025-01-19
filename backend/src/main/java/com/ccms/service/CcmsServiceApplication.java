package com.ccms.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

/**
 * Main class to start the CCMS (Customer Creditcard Management System)
 * application. This class is the entry point for the Spring Boot application
 * and enables essential services like component scanning and service discovery.
 */

@SpringBootApplication
@ComponentScan(basePackages = "com.ccms.service")
@EnableDiscoveryClient
public class CcmsServiceApplication {

	/**
	 * The main method is the entry point for running the Spring Boot application.
	 * 
	 * @param args Command line arguments passed during application startup.
	 */

	public static void main(String[] args) {
		SpringApplication.run(CcmsServiceApplication.class, args);
	}

}