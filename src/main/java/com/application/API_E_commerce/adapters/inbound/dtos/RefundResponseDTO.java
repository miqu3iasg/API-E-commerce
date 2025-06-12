package com.application.API_E_commerce.adapters.inbound.dtos;

import com.application.API_E_commerce.domain.refund.RefundReason;
import com.application.API_E_commerce.domain.refund.RefundStatus;

import java.math.BigDecimal;

public record RefundResponseDTO(
		String id,
		String paymentId,
		BigDecimal amount,
		RefundReason reason,
		RefundStatus status,
		String stripeRefundId
) {


	public RefundResponseDTO { }

}
