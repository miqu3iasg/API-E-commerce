package com.application.API_E_commerce.domain.order.orderitem.useCase;

import com.application.API_E_commerce.domain.order.orderitem.OrderItem;

import java.util.Optional;
import java.util.UUID;

public interface OrderItemUseCase {

	Optional<OrderItem> findOrderById (UUID orderItemId);
	// outros métodos
}
