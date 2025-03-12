package com.application.API_E_commerce.domain.cart;

public enum CartStatus {
  ACTIVE("active"), ABANDONED("abandoned"), COMPLETED("completed");

  private final String status;

  CartStatus(String status) {
    this.status = status;
  }

  public String getCartStatus() {
    return status;
  }
}