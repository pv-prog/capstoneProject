package com.ccms.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan(basePackages = "com.ccms.customer")
@EnableDiscoveryClient
public class CcmsCustomerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CcmsCustomerApplication.class, args);
	}

	@Bean
	//@LoadBalanced
	public RestTemplate restTemplate()
	{
		return new RestTemplate();
	}


	
}
