package com.application.API_E_commerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "com.application.API_E_commerce")
@EntityScan("com.application.API_E_commerce.adapters.outbound.entities")
public class ApiECommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiECommerceApplication.class, args);
	}

}
