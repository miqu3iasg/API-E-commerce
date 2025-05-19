package com.application.API_E_commerce.domain.order.orderitem.services;

import com.application.API_E_commerce.domain.order.orderitem.OrderItem;
import com.application.API_E_commerce.domain.order.orderitem.repository.OrderItemRepositoryPort;
import com.application.API_E_commerce.domain.order.orderitem.useCase.OrderItemUseCase;
import com.application.API_E_commerce.infrastructure.exceptions.order.orderItem.MissingOrderItemIdException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class OrderItemService implements OrderItemUseCase {

	private final OrderItemRepositoryPort orderItemRepositoryPort;

	public OrderItemService (OrderItemRepositoryPort orderItemRepositoryPort) {
		this.orderItemRepositoryPort = orderItemRepositoryPort;
	}

	@Override
	public Optional<OrderItem> findOrderById (UUID orderItemId) {
		validateInput(orderItemId);
		return orderItemRepositoryPort.findOrderItemById(orderItemId);
	}

	private void validateInput (UUID id) {
		if (id == null)
			throw new MissingOrderItemIdException("Order id cannot be null.");
	}

}
