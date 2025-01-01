package com.application.API_E_commerce.domain.order;

public enum OrderStatus {
  PENDING("pending"), PAYMENT("payment");

  private final String status;

  OrderStatus (String status) {
    this.status = status;
  }

  String getOrderStatus () {
    return status;
  }
}
