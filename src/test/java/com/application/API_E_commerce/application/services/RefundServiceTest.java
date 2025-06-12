package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.adapters.inbound.dtos.RefundRequestDTO;
import com.application.API_E_commerce.common.utils.mappers.RefundMapper;
import com.application.API_E_commerce.domain.payment.gateways.PaymentGatewayPort;
import com.application.API_E_commerce.domain.refund.PaymentRefund;
import com.application.API_E_commerce.domain.refund.RefundReason;
import com.application.API_E_commerce.domain.refund.RefundStatus;
import com.application.API_E_commerce.domain.refund.repository.RefundRepositoryPort;
import com.application.API_E_commerce.domain.refund.services.RefundService;
import com.stripe.model.Refund;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundServiceTest {

	@InjectMocks
	RefundService refundService;

	@Mock
	RefundRepositoryPort refundRepositoryPort;

	@Mock
	PaymentGatewayPort paymentGatewayPort;

	@Mock
	RefundMapper refundMapper;

	private RefundRequestDTO buildRefundRequest (String paymentIntentId, BigDecimal amount, RefundReason reason) {
		return new RefundRequestDTO(paymentIntentId, amount, reason);
	}

	private PaymentRefund buildPaymentRefund (String paymentIntentId, BigDecimal amount,
	                                          RefundReason reason, RefundStatus status, String stripeRefundId) {
		PaymentRefund refund = new PaymentRefund();
		refund.setId(UUID.randomUUID());
		refund.setAmount(amount);
		refund.setPaymentIntentId(paymentIntentId);
		refund.setReason(reason);
		refund.setStripeRefundId(stripeRefundId);
		refund.setStatus(status);
		refund.setCreatedAt(LocalDateTime.now());
		return refund;
	}

	@Nested
	@DisplayName("Create Refund Tests")
	class CreateRefundTests {

		@Nested
		@DisplayName("Successful Scenarios")
		class SuccessfulScenarios {

			@Test
			@DisplayName("Should create refund successfully when payment is valid and not refunded")
			void shouldCreateRefundSuccessfully () {
				final String paymentIntentId = "pi_test123";
				BigDecimal amount = BigDecimal.valueOf(50.0);
				RefundRequestDTO request = buildRefundRequest(paymentIntentId, amount, RefundReason.REQUESTED_BY_CUSTOMER);

				when(refundRepositoryPort.findRefundByPaymentIntentId(paymentIntentId))
						.thenReturn(Optional.empty());

				PaymentRefund mappedRefund = new PaymentRefund(
						paymentIntentId,
						amount,
						RefundReason.REQUESTED_BY_CUSTOMER,
						RefundStatus.PENDING
				);

				when(refundMapper.toEntity(request)).thenReturn(mappedRefund);

				Refund stripeRefund = new Refund();
				stripeRefund.setId("re_stripe123");
				when(paymentGatewayPort.processRefund(any(PaymentRefund.class))).thenReturn(stripeRefund);

				when(refundRepositoryPort.saveRefund(any(PaymentRefund.class))).thenAnswer(invocation -> invocation.getArgument(0));

				PaymentRefund result = refundService.createRefund(request);

				assertNotNull(result);
				assertEquals(paymentIntentId, result.getPaymentIntentId());
				assertEquals(amount, result.getAmount());
				assertEquals(RefundStatus.SUCCEEDED, result.getStatus());
				assertEquals("re_stripe123", result.getStripeRefundId());

			}

		}

		@Nested
		@DisplayName("Error Scenarios")
		class ErrorScenarios {

			@Test
			@DisplayName("Should throw exception when payment already has a refund")
			void shouldThrowExceptionWhenPaymentAlreadyRefunded () {
				final String paymentIntentId = "pi_test1223";

				RefundRequestDTO request = buildRefundRequest(paymentIntentId, BigDecimal.valueOf(50.0),
						RefundReason.REQUESTED_BY_CUSTOMER);

				PaymentRefund existingRefund = buildPaymentRefund(paymentIntentId, BigDecimal.valueOf(30.0),
						RefundReason.FRAUDULENT, RefundStatus.SUCCEEDED, "re_existing123");

				when(refundRepositoryPort.findRefundByPaymentIntentId(paymentIntentId))
						.thenReturn(Optional.of(existingRefund));

				assertThrows(IllegalStateException.class, () -> refundService.createRefund(request));

				verify(refundRepositoryPort).findRefundByPaymentIntentId(paymentIntentId);
				verify(paymentGatewayPort, never()).processRefund(any());
				verify(refundMapper, never()).toDomain(any());
				verify(refundRepositoryPort, never()).saveRefund(any());
			}

		}

		@Nested
		@DisplayName("Input Validation")
		class InputValidation {

			@Test
			@DisplayName("Should throw exception when refund amount is negative")
			void shouldThrowExceptionWhenRefundAmountIsNegative () {
				RefundRequestDTO request = buildRefundRequest("pi_test123", BigDecimal.valueOf(- 10.0),
						RefundReason.REQUESTED_BY_CUSTOMER);

				assertThrows(IllegalArgumentException.class, () -> refundService.createRefund(request));

				verifyNoInteractions(refundRepositoryPort, paymentGatewayPort, refundMapper);
			}

			@Test
			@DisplayName("Should throw exception when refund amount is zero")
			void shouldThrowExceptionWhenRefundAmountIsZero () {
				RefundRequestDTO request = buildRefundRequest("pi_test123", BigDecimal.ZERO,
						RefundReason.REQUESTED_BY_CUSTOMER);

				assertThrows(IllegalArgumentException.class, () -> refundService.createRefund(request));

				verifyNoInteractions(refundRepositoryPort, paymentGatewayPort, refundMapper);
			}

			@Test
			@DisplayName("Should throw exception when refund reason is null")
			void shouldThrowExceptionWhenRefundReasonIsNull () {
				RefundRequestDTO request = buildRefundRequest("pi_test123", BigDecimal.valueOf(50.0), null);

				assertThrows(IllegalArgumentException.class, () -> refundService.createRefund(request));

				verifyNoInteractions(refundRepositoryPort, paymentGatewayPort, refundMapper);
			}

			@Test
			@DisplayName("Should throw exception when request is null")
			void shouldThrowExceptionWhenRequestIsNull () {
				assertThrows(IllegalArgumentException.class, () -> refundService.createRefund(null));

				verifyNoInteractions(refundRepositoryPort, paymentGatewayPort, refundMapper);
			}

		}

	}

	@Nested
	@DisplayName("Get Refund Status Tests")
	class GetRefundStatusTests {

		@Test
		@DisplayName("Should get refund status from gateway")
		void shouldGetRefundStatusFromGateway () {
			UUID refundId = UUID.randomUUID();
			final String stripeRefundId = "re_test123";

			PaymentRefund refund = buildPaymentRefund("pi_test123", BigDecimal.valueOf(50.0),
					RefundReason.REQUESTED_BY_CUSTOMER, RefundStatus.PENDING, stripeRefundId);

			when(refundRepositoryPort.findRefundById(refundId))
					.thenReturn(Optional.of(refund));

			when(paymentGatewayPort.retrieveRefundStatus(stripeRefundId))
					.thenReturn(RefundStatus.SUCCEEDED);

			RefundStatus status = refundService.getRefundStatus(refundId);

			assertEquals(RefundStatus.SUCCEEDED, status);
			verify(refundRepositoryPort).findRefundById(refundId);
			verify(paymentGatewayPort).retrieveRefundStatus(stripeRefundId);
		}

		@Test
		@DisplayName("Should throw exception when refund ID is null")
		void shouldThrowExceptionWhenRefundIdIsNull () {
			assertThrows(IllegalArgumentException.class, () -> refundService.getRefundStatus(null));

			verifyNoInteractions(refundRepositoryPort, paymentGatewayPort);
		}

	}

}

