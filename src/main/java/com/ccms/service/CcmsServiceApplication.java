package com.ccms.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@ComponentScan(basePackages = "com.ccms.service")
@EnableDiscoveryClient
public class CcmsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CcmsServiceApplication.class, args);
	}

//	@Bean
//	//@LoadBalanced
//	public RestTemplate restTemplate()
//	{
//		return new RestTemplate();
//	}
//
//

}