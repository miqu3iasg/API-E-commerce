package com.application.API_E_commerce.domain.refund.useCase;

import com.application.API_E_commerce.adapters.inbound.dtos.RefundRequestDTO;
import com.application.API_E_commerce.domain.refund.PaymentRefund;
import com.application.API_E_commerce.domain.refund.RefundStatus;

import java.util.UUID;

public interface RefundUseCase {

	PaymentRefund createRefund (RefundRequestDTO request);

	RefundStatus getRefundStatus (UUID refundId);

}
