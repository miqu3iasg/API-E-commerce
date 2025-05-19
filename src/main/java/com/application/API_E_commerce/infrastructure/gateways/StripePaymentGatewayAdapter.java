package com.application.API_E_commerce.infrastructure.gateways;

import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.payment.gateways.PaymentGateway;
import com.application.API_E_commerce.infrastructure.exceptions.payment.CreatingCheckoutSessionException;
import com.application.API_E_commerce.infrastructure.exceptions.payment.ProcessingPaymentException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class StripePaymentGatewayAdapter implements PaymentGateway {

	private static final List<String> SUPPORTED_METHODS = Arrays.asList(
			"card", "boleto"
	);

	//Colocar DTO para request "CreatePaymentIntentRequestDTO"
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

}
