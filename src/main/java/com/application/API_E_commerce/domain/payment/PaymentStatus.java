package com.application.API_E_commerce.domain.payment;

public enum PaymentStatus {
  PAID("paid"), REJECTED("rejected"), PENDING("pending");

  private final String status;

  PaymentStatus (String status) {
    this.status = status;
  }

  String getPaymentStatus () {
    return status;
  }
}
