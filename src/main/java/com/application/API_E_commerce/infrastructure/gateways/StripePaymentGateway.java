package com.application.API_E_commerce.infrastructure.gateways;

import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.payment.PaymentGateway;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class StripePaymentGateway implements PaymentGateway {

  private static final List<String> SUPPORTED_METHODS = Arrays.asList(
          "card", "boleto", "pix", "paypal", "apple_pay", "google_pay"
  );

  @Override
  public PaymentIntent processPayment ( Payment paymentRequest ) {
    try {
      validatePaymentRequest(paymentRequest);

      PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
              .setCurrency(paymentRequest.getCurrency())
              .setAmount(paymentRequest.getAmountInCents())
              .setDescription(paymentRequest.getDescription());

      if ( SUPPORTED_METHODS.contains(paymentRequest.getPaymentMethod().getStripeMethod()) ) {
        paramsBuilder.addPaymentMethodType(paymentRequest.getPaymentMethod().getStripeMethod());
      }

      if (paymentRequest.getPaymentMethodId() != null && !paymentRequest.getPaymentMethodId().isEmpty()) {
        paramsBuilder.setPaymentMethod(paymentRequest.getPaymentMethodId());
      }

      PaymentIntentCreateParams params = paramsBuilder.build();

      return PaymentIntent.create(params);
    } catch ( StripeException e ) {
      throw new IllegalArgumentException("Error processing payment: " + e.getMessage(), e);
    }
  }

  private static void validatePaymentRequest ( Payment paymentRequest ) {
    if ( paymentRequest == null ) {
      throw new IllegalArgumentException("Payment request cannot be null");
    }
    if ( paymentRequest.getAmountInCents() == null ) {
      throw new IllegalArgumentException("Amount in cents is required");
    }
    if ( paymentRequest.getAmountInCents() <= 0 ) {
      throw new IllegalArgumentException("Amount in cents must be greater than 0");
    }
    if ( paymentRequest.getDescription() == null ) {
      throw new IllegalArgumentException("Description is required");
    }
    if ( paymentRequest.getCurrency() == null ) {
      throw new IllegalArgumentException("Currency is required");
    }
    if ( paymentRequest.getPaymentMethod() == null ) {
      throw new IllegalArgumentException("Payment method is required");
    }
  }

}
