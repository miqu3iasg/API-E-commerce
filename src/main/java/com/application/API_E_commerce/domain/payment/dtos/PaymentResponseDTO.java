package com.application.API_E_commerce.domain.payment.dtos;

import com.application.API_E_commerce.domain.payment.PaymentMethod;
import com.application.API_E_commerce.domain.payment.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentResponseDTO {

  private UUID id;
  private Long amountInCents;
  private String description;
  private PaymentMethod paymentMethod;
  private String currency;
  private PaymentStatus status;
  private LocalDateTime paymentDate;

  public PaymentResponseDTO ( UUID id, Long amountInCents, String description, PaymentMethod paymentMethod, String currency, PaymentStatus status, LocalDateTime paymentDate ) {
    this.id = id;
    this.amountInCents = amountInCents;
    this.description = description;
    this.paymentMethod = paymentMethod;
    this.currency = currency;
    this.status = status;
    this.paymentDate = paymentDate;
  }

  public UUID getId () {
    return id;
  }

  public void setId ( UUID id ) {
    this.id = id;
  }

  public Long getAmountInCents () {
    return amountInCents;
  }

  public void setAmountInCents ( Long amountInCents ) {
    this.amountInCents = amountInCents;
  }

  public String getDescription () {
    return description;
  }

  public void setDescription ( String description ) {
    this.description = description;
  }

  public PaymentMethod getPaymentMethod () {
    return paymentMethod;
  }

  public void setPaymentMethod ( PaymentMethod paymentMethod ) {
    this.paymentMethod = paymentMethod;
  }

  public String getCurrency () {
    return currency;
  }

  public void setCurrency ( String currency ) {
    this.currency = currency;
  }

  public PaymentStatus getStatus () {
    return status;
  }

  public void setStatus ( PaymentStatus status ) {
    this.status = status;
  }

  public LocalDateTime getPaymentDate () {
    return paymentDate;
  }

  public void setPaymentDate ( LocalDateTime paymentDate ) {
    this.paymentDate = paymentDate;
  }

}
