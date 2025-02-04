package com.application.API_E_commerce.domain.order.dtos;

import com.application.API_E_commerce.domain.order.OrderStatus;
import com.application.API_E_commerce.domain.order.orderitem.OrderItem;
import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.user.User;

import java.util.List;

public record CreateOrderCheckoutDTO(User user, OrderStatus orderStatus, List<OrderItem> items, Payment payment) {
}
