package com.application.API_E_commerce.domain.payment.gateways;

import com.application.API_E_commerce.domain.payment.Payment;
import com.stripe.model.PaymentIntent;

public interface PaymentGateway {

	PaymentIntent processPayment (Payment paymentRequest);

	String createCheckoutSession (Payment paymentRequest);

}
