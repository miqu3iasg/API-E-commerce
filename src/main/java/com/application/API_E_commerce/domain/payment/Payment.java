package com.application.API_E_commerce.domain.payment;

import com.application.API_E_commerce.domain.order.Order;

import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {

	private UUID id;
	private Order order;
	private Long amountInCents;
	private String description;
	private PaymentMethod paymentMethod;
	private String paymentMethodId;
	private String paymentIntentId;
	private String currency;
	private PaymentStatus status;
	private LocalDateTime paymentDate;

	public Payment () {
	}

	public Payment (UUID id, Order order, PaymentMethod paymentMethod, PaymentStatus status, LocalDateTime paymentDate) {
		this.id = id;
		this.order = order;
		this.paymentMethod = paymentMethod;
		this.status = status;
		this.paymentDate = paymentDate;
	}

	public UUID getId () {
		return id;
	}

	public void setId (UUID id) {
		this.id = id;
	}

	public Order getOrder () {
		return order;
	}

	public void setOrder (Order order) {
		this.order = order;
	}

	public PaymentMethod getPaymentMethod () {
		return paymentMethod;
	}

	public void setPaymentMethod (PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public PaymentStatus getStatus () {
		return status;
	}

	public void setStatus (PaymentStatus status) {
		this.status = status;
	}

	public LocalDateTime getPaymentDate () {
		return paymentDate;
	}

	public void setPaymentDate (LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getCurrency () {
		return currency;
	}

	public void setCurrency (String currency) {
		this.currency = currency;
	}

	public Long getAmountInCents () {
		return amountInCents;
	}

	public void setAmountInCents (Long amountInCents) {
		this.amountInCents = amountInCents;
	}

	public String getDescription () {
		return description;
	}

	public void setDescription (String description) {
		this.description = description;
	}

	public String getPaymentMethodId () {
		return paymentMethodId;
	}

	public void setPaymentMethodId (String paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
	}

	public String getPaymentIntentId () {
		return paymentIntentId;
	}

	public void setPaymentIntentId (String paymentIntentId) {
		this.paymentIntentId = paymentIntentId;
	}

}
