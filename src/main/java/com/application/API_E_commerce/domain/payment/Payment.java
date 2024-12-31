package com.application.API_E_commerce.domain.payment;

import com.application.API_E_commerce.domain.order.Order;

import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {
  private UUID id;
  private Order order;
  private String paymentMethod;
  private String status; // Paid, rejected...
  private LocalDateTime paymentDate;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public LocalDateTime getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(LocalDateTime paymentDate) {
    this.paymentDate = paymentDate;
  }
}
