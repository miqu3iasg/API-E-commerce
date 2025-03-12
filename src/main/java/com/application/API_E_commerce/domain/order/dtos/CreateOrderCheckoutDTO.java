package com.application.API_E_commerce.domain.order.dtos;

import com.application.API_E_commerce.domain.order.orderitem.OrderItem;
import com.application.API_E_commerce.domain.payment.PaymentMethod;
import com.application.API_E_commerce.domain.user.User;

import java.util.List;

public record CreateOrderCheckoutDTO(User user, List<OrderItem> items, PaymentMethod paymentMethod) {
}
