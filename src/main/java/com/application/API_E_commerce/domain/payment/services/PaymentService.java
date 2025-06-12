package com.application.API_E_commerce.domain.payment.services;

import com.application.API_E_commerce.common.utils.PaymentIntentStatus;
import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.payment.PaymentStatus;
import com.application.API_E_commerce.domain.payment.gateways.PaymentGatewayPort;
import com.application.API_E_commerce.domain.payment.repository.PaymentRepositoryPort;
import com.application.API_E_commerce.domain.payment.useCase.PaymentUseCase;
import com.application.API_E_commerce.infrastructure.exceptions.payment.*;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService implements PaymentUseCase {

	private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

	private final PaymentGatewayPort paymentGatewayPort;
	private final PaymentRepositoryPort paymentRepositoryPort;

	public PaymentService (PaymentGatewayPort paymentGatewayPort, PaymentRepositoryPort paymentRepositoryPort) {
		this.paymentGatewayPort = paymentGatewayPort;
		this.paymentRepositoryPort = paymentRepositoryPort;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public PaymentIntent processPayment (Payment paymentRequest) {
		validatePaymentRequest(paymentRequest);

		if (paymentRequest.getPaymentIntentId() != null && ! paymentRequest.getPaymentIntentId().isEmpty())
			throw new IllegalArgumentException("A payment intent has already been created for this payment.");

		if (paymentRequest.getStatus() != PaymentStatus.PENDING)
			throw new IllegalArgumentException("Payment status must be PENDING to process a payment.");

		PaymentIntent paymentIntent = paymentGatewayPort.processPayment(paymentRequest);

		if (paymentIntent == null || paymentIntent.getId() == null)
			throw new ProcessingPaymentException("Stripe returned null or invalid PaymentIntent.");

		paymentRequest.setPaymentIntentId(paymentIntent.getId());

		paymentRepositoryPort.savePayment(paymentRequest);

		return paymentIntent;
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
	@Transactional(rollbackOn = Exception.class)
	public String createCheckoutSession (Payment paymentRequest) throws StripeException {
		validatePaymentRequest(paymentRequest);

		if (paymentRequest.getStatus() != PaymentStatus.PENDING)
			throw new IllegalArgumentException("Payment status must be PENDING to process a payment.");

		String checkoutUrl = paymentGatewayPort.createCheckoutSession(paymentRequest);

		if (checkoutUrl == null || checkoutUrl.isEmpty())
			throw new ProcessingPaymentException("Stripe returned null or invalid checkout URL.");

		return checkoutUrl;
	}

	@Override
	public PaymentIntent confirmPayment (String paymentIntentId, String paymentMethodId) {
		if (paymentIntentId == null || paymentIntentId.isEmpty())
			throw new MissingPaymentIntentIdException("Payment intent ID is required");

		if (paymentMethodId == null || paymentMethodId.isEmpty())
			throw new MissingPaymentMethodIdException("Payment method ID is required");

		PaymentIntent paymentIntent = paymentGatewayPort.retrievePaymentIntent(paymentIntentId);

		PaymentIntentStatus status = PaymentIntentStatus.fromValue(paymentIntent.getStatus());

		if (status != PaymentIntentStatus.REQUIRES_CONFIRMATION && status != PaymentIntentStatus.REQUIRES_PAYMENT_METHOD) {
			log.warn("Payment intent {} is not in a state that requires confirmation: {}", paymentIntent.getId(), status);
			throw new IllegalArgumentException("Payment intent is not in a state that requires confirmation: " + status);
		}

		PaymentIntent confirmedPaymentIntent = paymentGatewayPort.confirmPayment(paymentIntent, paymentMethodId);

		if (confirmedPaymentIntent == null || confirmedPaymentIntent.getId() == null)
			throw new ProcessingPaymentException("Was returned null or invalid confirmed PaymentIntent.");

		return confirmedPaymentIntent;
	}

}
