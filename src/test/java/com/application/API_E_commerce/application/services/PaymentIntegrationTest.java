package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.PaymentUseCases;
import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.payment.PaymentMethod;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("dev")
class PaymentIntegrationTest {

  @Autowired
  PaymentUseCases paymentService;

  @Test
  void testPaymentProcessing () {
    Assertions.assertNotNull(Stripe.apiKey, "Stripe API Key should not be null");
  }

  @Test
  void shouldProcessPaymentSuccessfully () throws StripeException {
    Payment payment = new Payment();
    payment.setAmountInCents(5000L); // R$50,00
    payment.setCurrency("BRL");
    payment.setDescription("Test payment.");
    payment.setPaymentMethod(PaymentMethod.CARD);

    PaymentIntent paymentIntent = paymentService.processPayment(payment);

    assertNotNull(paymentIntent);
    assertEquals("requires_payment_method", paymentIntent.getStatus());

    PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
            .setPaymentMethod("pm_card_visa")
            .setReturnUrl("https://example.com/return")
            .build();
    paymentIntent = paymentIntent.confirm(confirmParams);

    assertNotNull(paymentIntent);
    assertEquals("succeeded", paymentIntent.getStatus());
    assertEquals(5000L, paymentIntent.getAmount());
    assertEquals("BRL", paymentIntent.getCurrency().toUpperCase());
  }

  @Test
  void shouldFailWhenInvalidPaymentDataIsProvided () {
    Payment payment = new Payment();
    payment.setAmountInCents(0L);
    payment.setCurrency("BRL");
    payment.setDescription("Invalid payment.");
    payment.setPaymentMethod(PaymentMethod.CARD);

    assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment(payment));
  }

  @Test
  void shouldFailWithDeclinedCard () throws StripeException {
    Payment payment = new Payment();
    payment.setAmountInCents(5000L);
    payment.setCurrency("BRL");
    payment.setDescription("Test payment.");
    payment.setPaymentMethod(PaymentMethod.CARD);

    PaymentIntent paymentIntent = paymentService.processPayment(payment);

    PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
            .setPaymentMethod("pm_card_chargeDeclined")
            .setReturnUrl("https://example.com/return")
            .build();

    assertThrows(StripeException.class, () -> paymentIntent.confirm(confirmParams));
  }

}
