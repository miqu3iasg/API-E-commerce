package com.application.API_E_commerce.config;

import com.stripe.Stripe;
import com.stripe.StripeClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class StripeConfiguration {

  @Value("${stripe.api.key}")
  private String stripeApiKey;

  public String getStripeApiKey () {
    return stripeApiKey;
  }

  @PostConstruct
  public void init () {
    if ( stripeApiKey == null || stripeApiKey.isEmpty() ) {
      throw new IllegalStateException("Stripe API Key is not set!");
    }
    Stripe.apiKey = stripeApiKey;
  }

}
