package com.application.API_E_commerce.adapters.outbound.entities.payment;

import com.application.API_E_commerce.adapters.outbound.entities.order.JpaOrderEntity;
import com.application.API_E_commerce.domain.payment.PaymentMethod;
import com.application.API_E_commerce.domain.payment.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_payments")
@AllArgsConstructor
@NoArgsConstructor
public class JpaPaymentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@OneToOne
	@JoinColumn(name = "order_id", nullable = true)
	private JpaOrderEntity order;
	private PaymentMethod paymentMethod;
	private String paymentMethodId;
	private String paymentIntentId;
	private String currency;
	private Long amountInCents;
	private String description;
	@Enumerated(EnumType.STRING)
	private PaymentStatus status;
	private LocalDateTime paymentDate;

	public UUID getId () {
		return id;
	}

	public void setId (UUID id) {
		this.id = id;
	}

	public JpaOrderEntity getOrder () {
		return order;
	}

	public void setOrder (JpaOrderEntity order) {
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
