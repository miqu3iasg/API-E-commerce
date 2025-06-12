package com.application.API_E_commerce.adapters.inbound.dtos;

import com.application.API_E_commerce.domain.refund.RefundReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RefundRequestDTO(
		@NotNull(message = "Payment intent ID is required")
		@Schema(
				description = "Unique identifier for the payment intent",
				example = "pi_1234567890abcdef",
				requiredMode = Schema.RequiredMode.REQUIRED
		)
		String paymentIntentId,
		@NotNull(message = "Amount is required")
		@Schema(
				description = "Amount to be refunded in cents",
				example = "1000",
				requiredMode = Schema.RequiredMode.REQUIRED
		)
		BigDecimal amount,
		@NotNull(message = "Refund reason is required")
		@Schema(
				description = "Reason for the refund",
				example = "Customer requested a refund because the product was defective",
				requiredMode = Schema.RequiredMode.REQUIRED
		)
		RefundReason reason
) {
}
