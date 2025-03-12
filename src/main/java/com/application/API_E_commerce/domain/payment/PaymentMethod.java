package com.application.API_E_commerce.domain.payment;

public enum PaymentMethod {
  CREDIT_CARD("credit card"), DEBIT_CARD("debit card"), PAYPAL("paypal"), PIX("pix");

  private final String paymentMethod;

  PaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }
}
