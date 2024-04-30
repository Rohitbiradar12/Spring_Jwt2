package com.thbs.applicationgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApplicationgatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationgatewayApplication.class, args);
	}

}
