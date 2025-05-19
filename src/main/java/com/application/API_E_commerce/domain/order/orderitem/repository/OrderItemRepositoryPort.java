package com.application.API_E_commerce.domain.order.orderitem.repository;

import com.application.API_E_commerce.domain.order.orderitem.OrderItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderItemRepositoryPort {

	OrderItem saveOrderItem (OrderItem orderItem);

	Optional<OrderItem> findOrderItemById (UUID id);

	List<OrderItem> findAllOrderItems ();

	void deleteOrderItemById (UUID orderItemId);

	void deleteOrderItem (OrderItem orderItem);

}
