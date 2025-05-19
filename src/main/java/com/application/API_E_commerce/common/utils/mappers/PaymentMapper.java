package com.application.API_E_commerce.common.utils.mappers;

import com.application.API_E_commerce.adapters.outbound.entities.payment.JpaPaymentEntity;
import com.application.API_E_commerce.domain.payment.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

	PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

	@Named("paymentToJpa")
	default JpaPaymentEntity paymentToJpa (Payment payment) {
		return payment == null ? null : PaymentMapper.INSTANCE.toJpa(payment);
	}

	@Mappings({
			@Mapping(source = "id", target = "id"),
			@Mapping(source = "order", target = "order", ignore = true),
			@Mapping(source = "amountInCents", target = "amountInCents"),
			@Mapping(source = "description", target = "description"),
			@Mapping(source = "paymentMethod", target = "paymentMethod"),
			@Mapping(source = "paymentMethodId", target = "paymentMethodId"),
			@Mapping(source = "currency", target = "currency"),
			@Mapping(source = "status", target = "status"),
			@Mapping(source = "paymentDate", target = "paymentDate"),
	})
	JpaPaymentEntity toJpa (Payment domain);

	@Named("paymentToDomain")
	default Payment paymentToDomain (JpaPaymentEntity jpaPayment) {
		return jpaPayment == null ? null : PaymentMapper.INSTANCE.toDomain(jpaPayment);
	}

	@Mappings({
			@Mapping(source = "id", target = "id"),
			@Mapping(source = "order", target = "order", ignore = true),
			@Mapping(source = "amountInCents", target = "amountInCents"),
			@Mapping(source = "description", target = "description"),
			@Mapping(source = "paymentMethod", target = "paymentMethod"),
			@Mapping(source = "paymentMethodId", target = "paymentMethodId"),
			@Mapping(source = "currency", target = "currency"),
			@Mapping(source = "status", target = "status"),
			@Mapping(source = "paymentDate", target = "paymentDate"),
	})
	Payment toDomain (JpaPaymentEntity jpa);

}
