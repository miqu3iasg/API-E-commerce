package com.application.API_E_commerce.adapters.outbound.repositories;

import com.application.API_E_commerce.adapters.outbound.entities.payment.JpaPaymentEntity;
import com.application.API_E_commerce.adapters.outbound.repositories.jpa.JpaPaymentRepository;
import com.application.API_E_commerce.common.utils.mappers.PaymentMapper;
import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.payment.repository.PaymentRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentRepositoryImplementation implements PaymentRepositoryPort {

	private final JpaPaymentRepository jpaPaymentRepository;
	private final PaymentMapper paymentMapper;

	public PaymentRepositoryImplementation (JpaPaymentRepository jpaPaymentRepository, PaymentMapper paymentMapper) {
		this.jpaPaymentRepository = jpaPaymentRepository;
		this.paymentMapper = paymentMapper;
	}

	@Override
	public Optional<Payment> findPaymentById (UUID id) {
		return Optional.ofNullable(jpaPaymentRepository.findById(id)
				.map(paymentMapper::toDomain)
				.orElseThrow(() -> new IllegalArgumentException("Payment was not found when searching for id in the repository.")));
	}

	@Override
	public Optional<Payment> findPaymentByOrderId (UUID orderId) {
		return Optional.empty();
	}

	@Override
	public Payment savePayment (Payment payment) {
		JpaPaymentEntity jpaPaymentEntityToSave = paymentMapper.toJpa(payment);

		JpaPaymentEntity jpaPaymentEntitySaved = jpaPaymentRepository.save(jpaPaymentEntityToSave);

		return paymentMapper.toDomain(jpaPaymentEntitySaved);
	}

	@Override
	public void deletePayment (UUID id) {
		jpaPaymentRepository.findById(id)
				.map(existingPaymentEntity -> {
					jpaPaymentRepository.deleteById(id);
					return existingPaymentEntity;
				})
				.orElseThrow(() -> new IllegalArgumentException("Payment was not found when searching for id in the delete product by id method."));
	}

}
