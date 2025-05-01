package com.application.API_E_commerce.domain.order.dtos;

import com.application.API_E_commerce.domain.order.OrderStatus;
import com.application.API_E_commerce.domain.order.orderitem.OrderItem;
import com.application.API_E_commerce.domain.payment.dtos.PaymentResponseDTO;
import com.application.API_E_commerce.domain.user.dtos.UserResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderResponseDTO {

  private UUID id;
  private OrderStatus status;
  private LocalDateTime orderDate;
  private BigDecimal totalValue;
  private String currency;
  private String description;
  private List<OrderItem> items;
  private PaymentResponseDTO payment;
  private UserResponseDTO user;

  public OrderResponseDTO ( UUID id, OrderStatus status, LocalDateTime orderDate, BigDecimal totalValue, String currency, String description, List<OrderItem> items, PaymentResponseDTO payment, UserResponseDTO user ) {
    this.id = id;
    this.status = status;
    this.orderDate = orderDate;
    this.totalValue = totalValue;
    this.currency = currency;
    this.description = description;
    this.items = items;
    this.payment = payment;
    this.user = user;
  }

  public UserResponseDTO getUser () {
    return user;
  }

  public void setUser ( UserResponseDTO user ) {
    this.user = user;
  }

  public PaymentResponseDTO getPayment () {
    return payment;
  }

  public void setPayment ( PaymentResponseDTO payment ) {
    this.payment = payment;
  }

  public List<OrderItem> getItems () {
    return items;
  }

  public void setItems ( List<OrderItem> items ) {
    this.items = items;
  }

  public String getDescription () {
    return description;
  }

  public void setDescription ( String description ) {
    this.description = description;
  }

  public String getCurrency () {
    return currency;
  }

  public void setCurrency ( String currency ) {
    this.currency = currency;
  }

  public BigDecimal getTotalValue () {
    return totalValue;
  }

  public void setTotalValue ( BigDecimal totalValue ) {
    this.totalValue = totalValue;
  }

  public LocalDateTime getOrderDate () {
    return orderDate;
  }

  public void setOrderDate ( LocalDateTime orderDate ) {
    this.orderDate = orderDate;
  }

  public OrderStatus getStatus () {
    return status;
  }

  public void setStatus ( OrderStatus status ) {
    this.status = status;
  }

  public UUID getId () {
    return id;
  }

  public void setId ( UUID id ) {
    this.id = id;
  }

}
