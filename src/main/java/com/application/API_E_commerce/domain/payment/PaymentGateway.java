package com.application.API_E_commerce.domain.payment;

import com.stripe.model.PaymentIntent;

public interface PaymentGateway {
  PaymentIntent processPayment( Payment paymentRequest );
}
