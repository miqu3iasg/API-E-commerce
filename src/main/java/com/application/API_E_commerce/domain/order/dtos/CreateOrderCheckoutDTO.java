package com.application.API_E_commerce.domain.order.dtos;

import com.application.API_E_commerce.domain.order.orderitem.OrderItem;
import com.application.API_E_commerce.domain.payment.PaymentMethod;
import com.application.API_E_commerce.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderCheckoutDTO(
		@NotNull(message = "The user is required!")
		@Schema(description = "The user who is placing the order")
		User user,

		@NotNull(message = "The items are required!")
		@Schema(description = "The items that are being ordered")
		List<OrderItem> items,

		@NotNull(message = "The payment method is required!")
		@Schema(description = "The payment method used for the order, can be " +
				"'CARD' or 'BOLETO")
		PaymentMethod paymentMethod
) {
}
