package com.application.API_E_commerce.domain.refund;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentRefund {

	private UUID id;
	private String paymentIntentId;
	private BigDecimal amount;
	private RefundReason reason;
	private RefundStatus status;
	private String stripeRefundId;
	private String failedReason;
	private LocalDateTime processedAt;
	private LocalDateTime failedAt;
	private LocalDateTime createdAt;

	public PaymentRefund () {
	}

	public PaymentRefund (UUID id, String paymentIntentId, BigDecimal amount,
	                      RefundReason reason,
	                      RefundStatus status, String stripeRefundId) {
		this.id = id;
		this.paymentIntentId = paymentIntentId;
		this.amount = amount;
		this.reason = reason;
		this.status = status;
		this.stripeRefundId = stripeRefundId;
		createdAt = LocalDateTime.now();
	}

	public PaymentRefund (UUID id, String paymentIntentId, BigDecimal amount,
	                      RefundReason reason, RefundStatus status, LocalDateTime createdAt) {
		this.id = id;
		this.paymentIntentId = paymentIntentId;
		this.amount = amount;
		this.reason = reason;
		this.status = status;
		this.createdAt = createdAt;
	}

	public PaymentRefund (String paymentIntentId, BigDecimal amount,
	                      RefundReason reason, RefundStatus status) {
		this.paymentIntentId = paymentIntentId;
		this.amount = amount;
		this.reason = reason;
		this.status = status;
	}

	public void markAsFailed (String reason) {
		status = RefundStatus.FAILED;
		failedReason = reason;
		failedAt = LocalDateTime.now();
	}

	public UUID getId () {
		return id;
	}

	public void setId (UUID id) {
		this.id = id;
	}

	public String getPaymentIntentId () {
		return paymentIntentId;
	}

	public void setPaymentIntentId (String paymentIntentId) {
		this.paymentIntentId = paymentIntentId;
	}

	public BigDecimal getAmount () {
		return amount;
	}

	public void setAmount (BigDecimal amount) {
		this.amount = amount;
	}

	public RefundReason getReason () {
		return reason;
	}

	public void setReason (RefundReason reason) {
		this.reason = reason;
	}

	public RefundStatus getStatus () {
		return status;
	}

	public void setStatus (RefundStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt () {
		return createdAt;
	}

	public void setCreatedAt (LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getStripeRefundId () {
		return stripeRefundId;
	}

	public void setStripeRefundId (String stripeRefundId) {
		this.stripeRefundId = stripeRefundId;
	}

	public LocalDateTime getProcessedAt () {
		return processedAt;
	}

	public void setProcessedAt (LocalDateTime processedAt) {
		this.processedAt = processedAt;
	}

	public LocalDateTime getFailedAt () {
		return failedAt;
	}

	public void setFailedAt (LocalDateTime failedAt) {
		this.failedAt = failedAt;
	}

	public String getFailedReason () {
		return failedReason;
	}

	public void setFailedReason (String failedReason) {
		this.failedReason = failedReason;
	}

}
