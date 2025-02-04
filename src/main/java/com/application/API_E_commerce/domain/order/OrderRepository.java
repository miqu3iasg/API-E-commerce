package com.application.API_E_commerce.domain.order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
  Order saveOrder(Order order);
  List<Order> findAllOrders();
  Optional<Order> findOrderById(UUID orderId);
  void deleteOrder(UUID orderId);
}
