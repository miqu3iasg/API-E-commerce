package com.application.API_E_commerce.domain.payment.repository;

import com.application.API_E_commerce.domain.payment.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepositoryPort {

	Optional<Payment> findPaymentById (UUID id);

	Optional<Payment> findPaymentByOrderId (UUID orderId);

	Payment savePayment (Payment payment);

	void deletePayment (UUID id);

	void deleteAllPayments ();

}
