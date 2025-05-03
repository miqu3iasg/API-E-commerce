package com.application.API_E_commerce.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI () {
		return new OpenAPI()
				.info(new Info()
						.title("E-Commerce API")
						.version("1.0.0")
						.description("API for managing users, products, payments, orders, carts, and categories in an e-commerce platform")
						.termsOfService("http://example.com/terms")
						.contact(new Contact()
								.name("API Support")
								.email("support@example.com")
								.url("http://example.com/support"))
						.license(new License()
								.name("Apache 2.0")
								.url("http://www.apache.org/licenses/LICENSE-2.0.html")));
	}

}
