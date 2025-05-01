package com.application.API_E_commerce.domain.payment;

import java.math.BigDecimal;

public class CreatePaymentIntentRequestDTO {
  private String currency;
  private BigDecimal amountInCents;
  private String description;
  private PaymentMethod paymentMethod;
  private String paymentMethodId;
}
