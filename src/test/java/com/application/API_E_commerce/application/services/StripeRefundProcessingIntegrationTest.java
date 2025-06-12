package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.adapters.inbound.dtos.RefundRequestDTO;
import com.application.API_E_commerce.domain.refund.PaymentRefund;
import com.application.API_E_commerce.domain.refund.RefundReason;
import com.application.API_E_commerce.domain.refund.RefundStatus;
import com.application.API_E_commerce.domain.refund.repository.RefundRepositoryPort;
import com.application.API_E_commerce.domain.refund.services.RefundService;
import com.stripe.Stripe;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class StripeRefundProcessingIntegrationTest {

	@Autowired
	RefundService refundService;

	@Autowired
	RefundRepositoryPort refundRepository;

	@Test
	@DisplayName("Should have a valid Stripe API key")
	void testPaymentProcessing () {
		Assertions.assertNotNull(Stripe.apiKey, "Stripe API Key should not be null");
	}

	/**
	 * This test verifies the successful processing of a refund using the real Stripe API in test mode.
	 * <p>
	 * It performs the following steps:
	 * - Uses a real Stripe PaymentIntent ID to initiate a refund.
	 * - Asserts that the refund was created successfully and has status SUCCEEDED.
	 * - Checks the refund status directly from Stripe and validates the result.
	 * - Ensures the refund is correctly saved in the database with consistent data.
	 * <p>
	 * Important:
	 * This test depends on a real Stripe PaymentIntent ID (in test mode). The ID provided must point to
	 * a valid test PaymentIntent that has not already been refunded. If the PaymentIntent has already
	 * been refunded, this test will fail because of the business rule that prevents multiple refunds
	 * for the same payment.
	 * <p>
	 * If the test fails, replace the 'stripePaymentId' value with a new valid test PaymentIntent ID
	 * that has not been refunded yet, available from your Stripe test dashboard
	 */
	@Test
	void shouldProcessRefundSuccessfullyAndVerifyStripeStatus () {
		final String stripePaymentId = "pi_3RKVDvG8UnEF8zOO07fWIcKB"; //Real Stripe PaymentIntent ID in test mode
		BigDecimal amount = BigDecimal.valueOf(10.0);

		RefundRequestDTO refundRequestDTO = new RefundRequestDTO(
				stripePaymentId,
				amount,
				RefundReason.REQUESTED_BY_CUSTOMER
		);

		PaymentRefund refund = refundService.createRefund(refundRequestDTO);

		assertNotNull(refund);
		assertNotNull(refund.getStripeRefundId());
		assertEquals(RefundStatus.SUCCEEDED, refund.getStatus());

		RefundStatus refundStatus = refundService.getRefundStatus(refund.getId());
		log.info("Refund status provided by Stripe: {}", refundStatus);
		assertEquals(RefundStatus.SUCCEEDED, refundStatus, "Refund status should be SUCCEEDED");

		PaymentRefund savedRefund = refundRepository.findRefundById(refund.getId())
				.orElseThrow(() -> new RuntimeException("Refund not found in repository"));

		assertNotNull(savedRefund);
		assertEquals(refund.getId(), savedRefund.getId());
		assertEquals(refund.getPaymentIntentId(), savedRefund.getPaymentIntentId());
		assertEquals(refund.getAmount().stripTrailingZeros(), savedRefund.getAmount().stripTrailingZeros());
		assertEquals(refund.getReason(), savedRefund.getReason());
		assertEquals(refund.getStatus(), savedRefund.getStatus());
		assertEquals(refund.getStripeRefundId(), savedRefund.getStripeRefundId());
	}

}
