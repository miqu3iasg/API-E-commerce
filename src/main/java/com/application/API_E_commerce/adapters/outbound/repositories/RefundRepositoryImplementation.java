package com.application.API_E_commerce.adapters.outbound.repositories;

import com.application.API_E_commerce.adapters.outbound.entities.refund.JpaRefundEntity;
import com.application.API_E_commerce.adapters.outbound.repositories.jpa.JpaRefundRepository;
import com.application.API_E_commerce.common.utils.mappers.RefundMapper;
import com.application.API_E_commerce.domain.refund.PaymentRefund;
import com.application.API_E_commerce.domain.refund.repository.RefundRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class RefundRepositoryImplementation implements RefundRepositoryPort {

	private static final Logger log = LoggerFactory.getLogger(RefundRepositoryImplementation.class);

	private final JpaRefundRepository jpaRefundRepository;
	private final RefundMapper refundMapper;

	public RefundRepositoryImplementation (JpaRefundRepository jpaRefundRepository, RefundMapper refundMapper) {
		this.jpaRefundRepository = jpaRefundRepository;
		this.refundMapper = refundMapper;
	}

	@Override
	public PaymentRefund saveRefund (PaymentRefund refund) {
		JpaRefundEntity jpaRefundEntityToSave = refundMapper.toJpa(refund);

		JpaRefundEntity jpaRefundEntitySaved = jpaRefundRepository.save(jpaRefundEntityToSave);

		return refundMapper.toDomain(jpaRefundEntitySaved);
	}

	@Override
	public Optional<PaymentRefund> findRefundByPaymentIntentId (String paymentIntentId) {
		Optional<PaymentRefund> refund = jpaRefundRepository.findByPaymentIntentId(paymentIntentId)
				.map(refundMapper::toDomain);

		if (refund.isEmpty())
			log.info("Refund not found for payment intent ID: {}", paymentIntentId);

		return refund;
	}

	@Override
	public Optional<PaymentRefund> findRefundById (UUID refundId) {
		return Optional.ofNullable(jpaRefundRepository.findById(refundId)
				.map(refundMapper::toDomain)
				.orElseThrow(() -> new IllegalArgumentException("Refund not found.")));
	}

	@Override
	public void deleteRefund (UUID refundId) {
		jpaRefundRepository.findById(refundId)
				.map(existingRefundEntity -> {
					jpaRefundRepository.deleteById(refundId);
					return existingRefundEntity;
				})
				.orElseThrow(() -> new IllegalArgumentException("Refund not found."));
	}

}
