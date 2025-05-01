package com.application.API_E_commerce.application.usecases;

import com.application.API_E_commerce.domain.payment.Payment;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface PaymentUseCases {

  PaymentIntent processPayment ( Payment payment ) throws StripeException;

  String createCheckoutSession ( Payment payment ) throws StripeException;

}
