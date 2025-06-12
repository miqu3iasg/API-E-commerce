package com.application.API_E_commerce.adapters.outbound.repositories.jpa;

import com.application.API_E_commerce.adapters.outbound.entities.refund.JpaRefundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRefundRepository extends JpaRepository<JpaRefundEntity, UUID> {

	Optional<JpaRefundEntity> findByPaymentIntentId (String paymentId);

}

