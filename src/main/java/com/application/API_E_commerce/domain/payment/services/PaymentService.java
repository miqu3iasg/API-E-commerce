package com.application.API_E_commerce.domain.payment.services;

import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.payment.gateways.PaymentGateway;
import com.application.API_E_commerce.domain.payment.useCase.PaymentUseCase;
import com.application.API_E_commerce.infrastructure.exceptions.payment.*;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PaymentService implements PaymentUseCase {

	private final PaymentGateway paymentGateway;

	public PaymentService (PaymentGateway paymentGateway) {
		this.paymentGateway = paymentGateway;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public PaymentIntent processPayment (Payment paymentRequest) {
		validatePaymentRequest(paymentRequest);
		return paymentGateway.processPayment(paymentRequest);

	}

	private static void validatePaymentRequest (Payment paymentRequest) {
		if (paymentRequest == null)
			throw new MissingPaymentRequestException("Payment request cannot be " +
					"null");
		if (paymentRequest.getAmountInCents() == null)
			throw new InvalidAmountException("Amount in cents is required");
		if (paymentRequest.getAmountInCents() <= 0)
			throw new InvalidAmountException("Amount in cents must be greater " +
					"than 0");
		if (paymentRequest.getDescription() == null)
			throw new MissingPaymentDescriptionException("Description is required");
		if (paymentRequest.getCurrency() == null)
			throw new MissingPaymentCurrencyException("Currency is required");
		if (paymentRequest.getPaymentMethod() == null)
			throw new MissingPaymentMethodException("Payment method is required");
	}

	@Override
	public String createCheckoutSession (Payment paymentRequest) throws StripeException {
		validatePaymentRequest(paymentRequest);
		return paymentGateway.createCheckoutSession(paymentRequest);
	}

}
