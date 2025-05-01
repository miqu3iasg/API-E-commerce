package com.application.API_E_commerce.infrastructure.configuration;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfiguration {

	@Value("${stripe.api.key}")
	private String stripeApiKey;

	public String getStripeApiKey () {
		return stripeApiKey;
	}

	@PostConstruct
	public void init () {
		if (stripeApiKey == null || stripeApiKey.isEmpty()) {
			throw new IllegalStateException("Stripe API Key is not set!");
		}
		Stripe.apiKey = stripeApiKey;
	}

}
