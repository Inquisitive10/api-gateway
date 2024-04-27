package com.SSAssignment.onboardify.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.SSAssignment.onboardify.apigateway.configuration")
@EnableFeignClients
@ConfigurationPropertiesScan("com.SSAssignment.onboardify.apigateway.configuration")
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
