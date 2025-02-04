package com.application.API_E_commerce.application.usecases;

import com.application.API_E_commerce.domain.payment.Payment;

public interface PaymentUseCases {
  Payment processPayment(Payment payment);
}
