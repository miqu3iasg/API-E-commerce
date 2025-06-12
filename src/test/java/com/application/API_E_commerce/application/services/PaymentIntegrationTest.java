package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.domain.order.useCase.OrderUseCase;
import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.payment.PaymentMethod;
import com.application.API_E_commerce.domain.payment.PaymentStatus;
import com.application.API_E_commerce.domain.payment.repository.PaymentRepositoryPort;
import com.application.API_E_commerce.domain.payment.useCase.PaymentUseCase;
import com.application.API_E_commerce.infrastructure.exceptions.payment.CreatingCheckoutSessionException;
import com.application.API_E_commerce.infrastructure.exceptions.payment.InvalidAmountException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class PaymentIntegrationTest {

	@Autowired
	PaymentUseCase paymentService;

	@Autowired
	PaymentRepositoryPort paymentRepository;

	@Autowired
	OrderUseCase orderService;

	private Payment validPayment () {
		Payment payment = new Payment();
		payment.setAmountInCents(5000L); // R$50,00
		payment.setCurrency("BRL");
		payment.setDescription("Test payment.");
		payment.setPaymentMethod(PaymentMethod.CARD);
		payment.setStatus(PaymentStatus.PENDING);
		return payment;
	}

	@Test
	@DisplayName("Should have a valid Stripe API key")
	void testPaymentProcessing () {
		Assertions.assertNotNull(Stripe.apiKey, "Stripe API Key should not be null");
	}

	@Nested
	@DisplayName("Process Payment Intent")
	class ProcessPaymentIntent {

		@Test
		@DisplayName("Should process payment successfully")
		void shouldProcessPaymentSuccessfully () throws StripeException {
			Payment payment = validPayment();

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

			assertThrows(InvalidAmountException.class,
					() -> paymentService.processPayment(payment));
		}

		@Test
		@DisplayName("Should fail with declined card")
		void shouldFailWithDeclinedCard () throws StripeException {
			Payment payment = validPayment();

			PaymentIntent paymentIntent = paymentService.processPayment(payment);

			PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
					.setPaymentMethod("pm_card_chargeDeclined")
					.setReturnUrl("https://example.com/return")
					.build();

			assertThrows(StripeException.class, () -> paymentIntent.confirm(confirmParams));
		}

	}

	@Nested
	@DisplayName("Create Payment Session")
	class CreatePaymentSession {

		@Test
		@DisplayName("Should create a valid checkout session")
		void shouldCreateValidCheckoutSession () throws StripeException {
			Payment payment = validPayment();

			String checkoutSessionUrl = paymentService.createCheckoutSession(payment);

			log.info("Checkout session URL: {}", checkoutSessionUrl);

			assertNotNull(checkoutSessionUrl);
			assertTrue(checkoutSessionUrl.startsWith("https://checkout.stripe.com/"));
		}

		@Test
		@DisplayName("Should throw when amount is null")
		void shouldThrowWhenAmountIsNull () {
			Payment payment = validPayment();
			payment.setAmountInCents(null);

			assertThrows(InvalidAmountException.class,
					() -> paymentService.createCheckoutSession(payment));
		}

		@Test
		@DisplayName("Should throw when currency is invalid")
		void shouldThrowWhenCurrencyIsInvalid () {
			Payment payment = validPayment();
			payment.setCurrency("XYZ"); // Invalid currency

			CreatingCheckoutSessionException exception = assertThrows(CreatingCheckoutSessionException.class,
					() -> paymentService.createCheckoutSession(payment));

			assertTrue(exception.getMessage().contains("currency") || exception.getMessage().contains("StripeException"));
		}

	}

}
