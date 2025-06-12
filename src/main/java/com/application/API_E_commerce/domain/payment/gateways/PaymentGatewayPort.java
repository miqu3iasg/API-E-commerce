package com.application.API_E_commerce.domain.payment.gateways;

import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.refund.PaymentRefund;
import com.application.API_E_commerce.domain.refund.RefundStatus;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;

public interface PaymentGatewayPort {

	PaymentIntent processPayment (Payment paymentRequest);

	PaymentIntent confirmPayment (PaymentIntent paymentIntent, String paymentMethodId);

	PaymentIntent retrievePaymentIntent (String paymentIntentId);

	String createCheckoutSession (Payment paymentRequest);

	Refund processRefund (PaymentRefund paymentRefund);

	RefundStatus retrieveRefundStatus (String paymentIntentId);

}
