package com.application.API_E_commerce.domain.order.useCase;

import com.application.API_E_commerce.adapters.inbound.dtos.CreateOrderCheckoutDTO;
import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.order.OrderStatus;
import com.stripe.exception.StripeException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderUseCase {

	Order createOrderCheckout (CreateOrderCheckoutDTO createOrderCheckoutRequest) throws StripeException;

	List<Order> fetchAllOrderHistory ();

	List<Order> fetchOrderHistoryByUser (UUID userId);

	OrderStatus getOrderStatus (UUID orderId);

	Optional<Order> findOrderById (UUID orderId);

	void cancelOrder (UUID orderId);

}
