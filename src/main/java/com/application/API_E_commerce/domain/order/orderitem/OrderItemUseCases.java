package com.application.API_E_commerce.domain.order.orderitem;

import java.util.Optional;
import java.util.UUID;

public interface OrderItemUseCases {
  Optional<OrderItem> findOrderById(UUID orderItemId);
  // outros métodos
}
