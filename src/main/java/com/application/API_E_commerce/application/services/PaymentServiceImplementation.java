package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.PaymentUseCases;
import com.application.API_E_commerce.domain.payment.Payment;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImplementation implements PaymentUseCases {
  @Override
  public Payment processPayment(Payment payment) {
    System.out.println("Processing payment...");
    return payment;
  }
}
