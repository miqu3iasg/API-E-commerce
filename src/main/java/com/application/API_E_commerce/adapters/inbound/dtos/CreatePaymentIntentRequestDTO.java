package com.application.API_E_commerce.adapters.inbound.dtos;

import com.application.API_E_commerce.domain.payment.PaymentMethod;

import java.math.BigDecimal;

public class CreatePaymentIntentRequestDTO {

	private String currency;
	private BigDecimal amountInCents;
	private String description;
	private PaymentMethod paymentMethod;
	private String paymentMethodId;

}
