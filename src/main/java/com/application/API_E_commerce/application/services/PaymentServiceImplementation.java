package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.PaymentUseCases;
import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.payment.PaymentGateway;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImplementation implements PaymentUseCases {

  private final PaymentGateway paymentGateway;

  public PaymentServiceImplementation ( PaymentGateway paymentGateway ) {
    this.paymentGateway = paymentGateway;
  }

  @Override
  @Transactional(rollbackOn = Exception.class)
  public PaymentIntent processPayment ( Payment paymentRequest ) {
    return paymentGateway.processPayment(paymentRequest);
  }

  @Override
  public String createCheckoutSession ( Payment payment ) throws StripeException {
    return paymentGateway.createCheckoutSession(payment);
  }

}
