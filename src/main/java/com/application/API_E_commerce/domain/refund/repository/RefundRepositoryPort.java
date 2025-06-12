package com.application.API_E_commerce.domain.refund.repository;

import com.application.API_E_commerce.domain.refund.PaymentRefund;

import java.util.Optional;
import java.util.UUID;

public interface RefundRepositoryPort {

	PaymentRefund saveRefund (PaymentRefund refund);

	Optional<PaymentRefund> findRefundById (UUID id);

	void deleteRefund (UUID id);

	Optional<PaymentRefund> findRefundByPaymentIntentId (String paymentIntentId);

}
