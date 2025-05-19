package com.application.API_E_commerce.adapters.outbound.repositories;

import com.application.API_E_commerce.adapters.outbound.repositories.jpa.JpaOrderItemRepository;
import com.application.API_E_commerce.common.utils.mappers.OrderItemMapper;
import com.application.API_E_commerce.domain.order.orderitem.OrderItem;
import com.application.API_E_commerce.domain.order.orderitem.repository.OrderItemRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class OrderItemRepositoryImplementation implements OrderItemRepositoryPort {

	private final JpaOrderItemRepository jpaOrderItemRepository;
	private final OrderItemMapper orderItemMapper;

	public OrderItemRepositoryImplementation (JpaOrderItemRepository jpaOrderItemRepository, OrderItemMapper orderItemMapper) {
		this.jpaOrderItemRepository = jpaOrderItemRepository;
		this.orderItemMapper = orderItemMapper;
	}

	@Override
	public OrderItem saveOrderItem (OrderItem orderItem) {
		return null;
	}

	@Override
	public Optional<OrderItem> findOrderItemById (UUID orderItemId) {
		return Optional.ofNullable(jpaOrderItemRepository.findById(orderItemId)
				.map(orderItemMapper::toDomain)
				.orElseThrow(() -> new IllegalArgumentException("Order item not found.")));
	}

	@Override
	public List<OrderItem> findAllOrderItems () {
		return List.of();
	}

	@Override
	public void deleteOrderItemById (UUID id) {

	}

	@Override
	public void deleteOrderItem (OrderItem orderItem) {

	}

}
