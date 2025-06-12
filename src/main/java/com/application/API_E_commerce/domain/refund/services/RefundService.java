package com.application.API_E_commerce.domain.refund.services;

import com.application.API_E_commerce.adapters.inbound.dtos.RefundRequestDTO;
import com.application.API_E_commerce.common.utils.mappers.RefundMapper;
import com.application.API_E_commerce.domain.payment.gateways.PaymentGatewayPort;
import com.application.API_E_commerce.domain.refund.PaymentRefund;
import com.application.API_E_commerce.domain.refund.RefundStatus;
import com.application.API_E_commerce.domain.refund.repository.RefundRepositoryPort;
import com.application.API_E_commerce.domain.refund.useCase.RefundUseCase;
import com.application.API_E_commerce.infrastructure.exceptions.refund.ProcessingRefundException;
import com.application.API_E_commerce.infrastructure.exceptions.refund.RefundNotFoundException;
import com.stripe.model.Refund;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefundService implements RefundUseCase {

	private static final Logger log = LoggerFactory.getLogger(RefundService.class);

	private final RefundRepositoryPort refundRepositoryPort;
	private final PaymentGatewayPort paymentGatewayPort;
	private final RefundMapper refundMapper;

	public RefundService (RefundRepositoryPort refundRepositoryPort, PaymentGatewayPort paymentGatewayPort, RefundMapper refundMapper) {
		this.refundRepositoryPort = refundRepositoryPort;
		this.paymentGatewayPort = paymentGatewayPort;
		this.refundMapper = refundMapper;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public PaymentRefund createRefund (RefundRequestDTO request) {
		validateRefundRequest(request);

		log.info("Starting refund creation for paymentId: {}", request.paymentIntentId());

		refundRepositoryPort.findRefundByPaymentIntentId(request.paymentIntentId())
				.ifPresent(existingRefund -> {
					if (existingRefund.getStatus() == RefundStatus.PENDING || existingRefund.getStatus() == RefundStatus.FAILED)
						log.info("Found existing pending refund for paymentId: {}, reusing it.", request.paymentIntentId());
					else {
						log.warn("Refund for paymentId {} already exists with status: {}. " +
								"Skipping creation of a new refund.", request.paymentIntentId(), existingRefund.getStatus());
						throw new IllegalStateException("Refund for this payment already exists with status: " + existingRefund.getStatus());
					}
				});

		PaymentRefund refund = refundMapper.toEntity(request);

		refund.setStatus(RefundStatus.PROCESSING);

		try {
			Refund stripeRefund = paymentGatewayPort.processRefund(refund);

			refund.setStripeRefundId(stripeRefund.getId());

			refund.setStatus(RefundStatus.SUCCEEDED);
			refund.setProcessedAt(LocalDateTime.now());

			log.info("Refund successfully processed for paymentId: {}, stripeRefundId: {}",
					refund.getPaymentIntentId(), stripeRefund.getId());

		} catch (Exception ex) {
			log.error("Unexpected error while processing refund for paymentId {}: " +
					"{}", refund.getPaymentIntentId(), ex.getMessage(), ex);

			refund.markAsFailed("Stripe refund processing failed due to an exception thrown by the external gateway. " +
					"Exception message: " + ex.getMessage());

			refundRepositoryPort.saveRefund(refund);

			throw new ProcessingRefundException("Failed to process refund for payment ID " +
					refund.getPaymentIntentId() + " due to gateway error.", ex);
		}

		return refundRepositoryPort.saveRefund(refund);
	}

	private static void validateRefundRequest (RefundRequestDTO request) {
		if (request == null)
			throw new IllegalArgumentException("Refund request cannot be null");
		if (request.paymentIntentId() == null || request.paymentIntentId().isBlank())
			throw new IllegalArgumentException("Payment ID cannot be null or empty");
		if (request.amount() == null || request.amount().signum() <= 0)
			throw new IllegalArgumentException("Refund amount must be positive");
		if (request.reason() == null)
			throw new IllegalArgumentException("Refund reason cannot be null or empty");
	}

	@Override
	public RefundStatus getRefundStatus (UUID refundId) {
		if (refundId == null || refundId.toString().isBlank())
			throw new IllegalArgumentException("Refund ID cannot be null");

		PaymentRefund refund = refundRepositoryPort.findRefundById(refundId)
				.orElseThrow(() -> new RefundNotFoundException("Refund with ID " + refundId + " not found"));

		log.info("Retrieving refund status for refundId: {}", refundId);
		RefundStatus refundStatus = paymentGatewayPort.retrieveRefundStatus(refund.getStripeRefundId());

		if (refundStatus == null) {
			log.warn("Stripe refund status for refundId {} is null or empty.", refundId);
			return null;
		}

		log.info("Stripe refund status for refundId {}: {}", refundId, refundStatus);
		return refundStatus;
	}

}
