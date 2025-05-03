package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.domain.order.orderitem.OrderItem;
import com.application.API_E_commerce.domain.order.orderitem.OrderItemRepository;
import com.application.API_E_commerce.domain.order.orderitem.OrderItemUseCases;
import com.application.API_E_commerce.infrastructure.exceptions.order.orderItem.MissingOrderItemIdException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class OrderItemServiceImplementation implements OrderItemUseCases {

	private final OrderItemRepository orderItemRepository;

	public OrderItemServiceImplementation (OrderItemRepository orderItemRepository) {
		this.orderItemRepository = orderItemRepository;
	}

	@Override
	public Optional<OrderItem> findOrderById (UUID orderItemId) {
		validateInput(orderItemId);
		return orderItemRepository.findOrderItemById(orderItemId);
	}

	private void validateInput (UUID id) {
		if (id == null) {
			throw new MissingOrderItemIdException("Order id cannot be null.");
		}
	}

}
