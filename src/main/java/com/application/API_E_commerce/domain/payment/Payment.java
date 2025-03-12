package com.application.API_E_commerce.domain.payment;

import com.application.API_E_commerce.domain.order.Order;

import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {
  private UUID id;
  private Order order;
  private PaymentMethod paymentMethod;
  private PaymentStatus status;
  private LocalDateTime paymentDate;

  public Payment() {
  }

  public Payment(UUID id, Order order, PaymentMethod paymentMethod, PaymentStatus status, LocalDateTime paymentDate) {
    this.id = id;
    this.order = order;
    this.paymentMethod = paymentMethod;
    this.status = status;
    this.paymentDate = paymentDate;
  }

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

  public PaymentMethod getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(PaymentMethod paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
  }

  public LocalDateTime getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(LocalDateTime paymentDate) {
    this.paymentDate = paymentDate;
  }
}
