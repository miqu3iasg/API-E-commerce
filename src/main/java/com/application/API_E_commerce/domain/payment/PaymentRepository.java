package com.application.API_E_commerce.domain.payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
  Optional<Payment> findPaymentById(UUID id);
  Optional<Payment> findPaymentByOrderId(UUID orderId);
  Payment savePayment(Payment payment);
  void deletePayment(UUID id);
}
