package com.example.MicroService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGateDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGateDemoApplication.class, args);
	}

}
