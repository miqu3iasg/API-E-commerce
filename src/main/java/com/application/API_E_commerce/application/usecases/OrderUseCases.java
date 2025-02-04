package com.application.API_E_commerce.application.usecases;

import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.order.OrderStatus;
import com.application.API_E_commerce.domain.order.dtos.CreateOrderCheckoutDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderUseCases {
  Order createOrderCheckout(CreateOrderCheckoutDTO createOrderCheckoutRequest);
  List<Order> fetchAllOrderHistory();
  List<Order> fetchOrderHistoryByUser(UUID userId);
  OrderStatus getOrderStatus(UUID orderId);
  Optional<Order> findOrderById(UUID orderId);
  void cancelOrder(UUID orderId);
}
