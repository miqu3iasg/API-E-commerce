package com.application.API_E_commerce.domain.order;

import com.application.API_E_commerce.domain.order.orderitem.OrderItem;
import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order {
  private UUID id;
  private User user;
  private OrderStatus status;
  private LocalDateTime orderDate;
  private BigDecimal totalValue;
  private String currency;
  private String description;
  private List<OrderItem> items;
  private Payment payment;

  public Order() {
  }

  public Order(User user, List<OrderItem> items, Payment payment) {
    this.user = user;
    this.items = items;
    this.payment = payment;
  }

  public Order(UUID id, User user, OrderStatus status, LocalDateTime orderDate, BigDecimal totalValue, List<OrderItem> items, Payment payment) {
    this.id = id;
    this.user = user;
    this.status = status;
    this.orderDate = orderDate;
    this.totalValue = totalValue;
    this.items = items;
    this.payment = payment;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public LocalDateTime getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(LocalDateTime orderDate) {
    this.orderDate = orderDate;
  }

  public BigDecimal getTotalValue() {
    return totalValue;
  }

  public void setTotalValue(BigDecimal totalValue) {
    this.totalValue = totalValue;
  }

  public List<OrderItem> getItems() {
    return items;
  }

  public void setItems(List<OrderItem> items) {
    this.items = items;
  }

  public Payment getPayment() {
    return payment;
  }

  public void setPayment(Payment payment) {
    this.payment = payment;
  }

  public String getCurrency () {
    return currency;
  }

  public void setCurrency ( String currency ) {
    this.currency = currency;
  }

  public String getDescription () {
    return description;
  }

  public void setDescription ( String description ) {
    this.description = description;
  }

}
