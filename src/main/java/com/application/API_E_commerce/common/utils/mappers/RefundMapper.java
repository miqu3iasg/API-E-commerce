package com.application.API_E_commerce.common.utils.mappers;

import com.application.API_E_commerce.adapters.inbound.dtos.RefundRequestDTO;
import com.application.API_E_commerce.adapters.inbound.dtos.RefundResponseDTO;
import com.application.API_E_commerce.adapters.outbound.entities.refund.JpaRefundEntity;
import com.application.API_E_commerce.domain.refund.PaymentRefund;
import com.application.API_E_commerce.domain.refund.RefundStatus;
import org.springframework.stereotype.Component;

@Component
public class RefundMapper {

	public PaymentRefund toEntity (RefundRequestDTO request) {
		return new PaymentRefund(
				request.paymentIntentId(),
				request.amount(),
				request.reason(),
				RefundStatus.PENDING
		);
	}

	public JpaRefundEntity toJpa (PaymentRefund paymentRefund) {
		return new JpaRefundEntity(
				paymentRefund.getId(),
				paymentRefund.getPaymentIntentId(),
				paymentRefund.getAmount(),
				paymentRefund.getReason(),
				paymentRefund.getStatus(),
				paymentRefund.getStripeRefundId()
		);
	}

	public PaymentRefund toDomain (JpaRefundEntity jpaRefundEntity) {
		return new PaymentRefund(
				jpaRefundEntity.getId(),
				jpaRefundEntity.getPaymentIntentId(),
				jpaRefundEntity.getAmount(),
				jpaRefundEntity.getReason(),
				jpaRefundEntity.getStatus(),
				jpaRefundEntity.getStripeRefundId()
		);
	}

	public RefundResponseDTO toResponse (PaymentRefund paymentRefund) {
		return new RefundResponseDTO(
				paymentRefund.getId().toString(),
				paymentRefund.getPaymentIntentId(),
				paymentRefund.getAmount(),
				paymentRefund.getReason(),
				paymentRefund.getStatus(),
				paymentRefund.getStripeRefundId()
		);
	}

}
