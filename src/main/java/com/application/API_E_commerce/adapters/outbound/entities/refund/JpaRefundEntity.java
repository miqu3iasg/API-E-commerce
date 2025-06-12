package com.application.API_E_commerce.adapters.outbound.entities.refund;

import com.application.API_E_commerce.domain.refund.RefundReason;
import com.application.API_E_commerce.domain.refund.RefundStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_refunds")
@Getter
@Setter
public class JpaRefundEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
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

	public JpaRefundEntity (UUID id, String paymentIntentId, BigDecimal amount,
	                        RefundReason reason, RefundStatus status,
	                        String stripeRefundId) {
		this.id = id;
		this.paymentIntentId = paymentIntentId;
		this.amount = amount;
		this.reason = reason;
		this.status = status;
		this.stripeRefundId = stripeRefundId;
		createdAt = LocalDateTime.now();
	}

	public JpaRefundEntity () {
	}

}
