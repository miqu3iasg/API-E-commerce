package com.application.API_E_commerce.infrastructure.gateways;

import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.payment.gateways.PaymentGatewayPort;
import com.application.API_E_commerce.domain.refund.PaymentRefund;
import com.application.API_E_commerce.domain.refund.RefundReason;
import com.application.API_E_commerce.domain.refund.RefundStatus;
import com.application.API_E_commerce.infrastructure.exceptions.payment.CreatingCheckoutSessionException;
import com.application.API_E_commerce.infrastructure.exceptions.payment.ProcessingPaymentException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class StripePaymentGatewayPortAdapter implements PaymentGatewayPort {

	private static final Logger log = LoggerFactory.getLogger(StripePaymentGatewayPortAdapter.class);

	private static final List<String> SUPPORTED_METHODS = Arrays.asList(
			"card", "boleto"
	);

	@Override
	public PaymentIntent processPayment (Payment paymentRequest) {
		try {
			PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
					.setCurrency(paymentRequest.getCurrency())
					.setAmount(paymentRequest.getAmountInCents())
					.setDescription(paymentRequest.getDescription());

			if (SUPPORTED_METHODS.contains(paymentRequest.getPaymentMethod().getStripeMethod()))
				paramsBuilder.addPaymentMethodType(paymentRequest.getPaymentMethod().getStripeMethod());

			if (paymentRequest.getPaymentMethodId() != null && ! paymentRequest.getPaymentMethodId().isEmpty())
				paramsBuilder.setPaymentMethod(paymentRequest.getPaymentMethodId());

			PaymentIntentCreateParams params = paramsBuilder.build();

			return PaymentIntent.create(params);
		} catch (StripeException e) {
			throw new ProcessingPaymentException("Error processing payment: " + e.getMessage(),
					e);
		}
	}

	@Override
	public String createCheckoutSession (Payment paymentRequest) {
		try {
			SessionCreateParams.Builder sessionCreateParams = SessionCreateParams.builder()
					.setMode(SessionCreateParams.Mode.PAYMENT)
					.setSuccessUrl("https://example.com/success")
					.setCancelUrl("https://example.com/cancel")
					.addLineItem(
							SessionCreateParams.LineItem.builder()
									.setQuantity(1L)
									.setPriceData(
											SessionCreateParams.LineItem.PriceData.builder()
													.setCurrency(paymentRequest.getCurrency())
													.setUnitAmount(paymentRequest.getAmountInCents())
													.setProductData(
															SessionCreateParams.LineItem.PriceData.ProductData.builder()
																	.setName(paymentRequest.getDescription())
																	.build()
													).build()
									).build()
					);

			for (String method : SUPPORTED_METHODS)
				sessionCreateParams.addPaymentMethodType(SessionCreateParams.PaymentMethodType.valueOf(method.toUpperCase()));
			return Session.create(sessionCreateParams.build()).getUrl();
		} catch (StripeException e) {
			throw new CreatingCheckoutSessionException("Error creating checkout " +
					"session: " + e.getMessage(),
					e);
		}
	}

	@Override
	public PaymentIntent retrievePaymentIntent (String paymentIntentId) {
		try {
			PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

			if (paymentIntent == null)
				throw new IllegalArgumentException("Payment intent not found: " + paymentIntentId);

			return paymentIntent;
		} catch (StripeException e) {
			log.error("Error retrieving payment intent: {}", e.getMessage());
		}
		return null;
	}

	@Override
	public PaymentIntent confirmPayment (PaymentIntent paymentIntent, String paymentMethodId) {
		try {
			String paymentMethod = getReadablePaymentMethodType(paymentMethodId);

			PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
					.setPaymentMethod(paymentMethod)
					.build();

			paymentIntent = paymentIntent.confirm(confirmParams);

			return paymentIntent;
		} catch (StripeException e) {
			throw new IllegalArgumentException("Error confirming payment: " + e.getMessage(), e);
		}
	}

	private String getReadablePaymentMethodType (String paymentMethodId) {
		try {
			PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);

			if (paymentMethod == null)
				throw new IllegalArgumentException("Payment method not found: " + paymentMethodId);

			String type = paymentMethod.getType();

			if ("card".equals(type) && paymentMethod.getCard() != null) {
				String brand = paymentMethod.getCard().getBrand();
				return "pm_" + type + "_" + brand;
			}

			return "pm_" + type;
		} catch (StripeException e) {
			throw new IllegalArgumentException("Error when searching for payment method" + e.getMessage(), e);
		}
	}

	@Override
	public Refund processRefund (PaymentRefund refund) {
		if (refund.getStatus() != RefundStatus.PROCESSING)
			throw new IllegalStateException("Refund must be in PROCESSING state to be processed by gateway");

		try {
			RefundCreateParams.Reason stripeReason = mapToStripeReason(refund.getReason());

			RefundCreateParams refundCreateParams = RefundCreateParams.builder()
					.setPaymentIntent(refund.getPaymentIntentId())
					.setAmount(toCents(refund.getAmount()))
					.setReason(stripeReason)
					.build();

			return Refund.create(refundCreateParams);

		} catch (StripeException e) {
			throw new IllegalArgumentException("Stripe error: " + e.getMessage(), e);
		}
	}

	private RefundCreateParams.Reason mapToStripeReason (RefundReason reason) {
		return switch (reason) {
			case DUPLICATED -> RefundCreateParams.Reason.DUPLICATE;
			case FRAUDULENT -> RefundCreateParams.Reason.FRAUDULENT;
			case REQUESTED_BY_CUSTOMER ->
					RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER;
			case NONE -> null;
		};
	}

	private long toCents (BigDecimal amount) {
		return amount.multiply(BigDecimal.valueOf(100)).longValue();
	}

	@Override
	public RefundStatus retrieveRefundStatus (String stripeRefundId) {
		try {
			Refund refund = Refund.retrieve(stripeRefundId);
			return mapStripeStatusToRefundStatus(refund.getStatus());
		} catch (StripeException e) {
			throw new IllegalArgumentException("Error retrieving payment status: " + e.getMessage());
		}
	}

	private RefundStatus mapStripeStatusToRefundStatus (String stripeStatus) {
		return switch (stripeStatus.toLowerCase()) {
			case "succeeded", "completed" -> RefundStatus.SUCCEEDED;
			case "processing" -> RefundStatus.PROCESSING;
			case "pending" -> RefundStatus.PENDING;
			case "requires_action" -> RefundStatus.ACTION_REQUIRED;
			case "canceled" -> RefundStatus.REJECTED;
			case "failed" -> RefundStatus.FAILED;
			default -> {
				log.warn("Unknown Stripe refund status: {}", stripeStatus);
				yield RefundStatus.UNKNOWN;
			}
		};
	}

}
