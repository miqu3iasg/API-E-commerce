package com.application.API_E_commerce.utils.converters;

import com.application.API_E_commerce.adapters.outbound.entities.order.JpaOrderEntity;
import com.application.API_E_commerce.adapters.outbound.entities.order.JpaOrderItemEntity;
import com.application.API_E_commerce.adapters.outbound.entities.payment.JpaPaymentEntity;
import com.application.API_E_commerce.adapters.outbound.entities.user.JpaUserEntity;
import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.order.orderitem.OrderItem;
import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.utils.mappers.OrderItemMapper;
import com.application.API_E_commerce.utils.mappers.PaymentMapper;
import com.application.API_E_commerce.utils.mappers.UserMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderConverter {
  private final UserMapper userMapper;
  private final OrderItemMapper orderItemMapper;
  private final PaymentMapper paymentMapper;

  public OrderConverter(
          UserMapper userMapper,
          PaymentMapper paymentMapper,
          OrderItemMapper orderItemMapper
  ) {
    this.userMapper = userMapper;
    this.paymentMapper = paymentMapper;
    this.orderItemMapper = orderItemMapper;
  }

  public JpaOrderEntity toJpa (Order domain) {
    if (domain == null) return null; // Exceções personalizadas

    JpaUserEntity jpaUserEntity = domain.getUser() != null
            ? userMapper.toJpa(domain.getUser())
            : null;

    List<JpaOrderItemEntity> jpaOrderItemEntityList = domain.getItems() != null
            ? domain.getItems().stream().map(orderItemMapper::toJpa).toList()
            : null;

    JpaPaymentEntity jpaPaymentEntity = domain.getPayment() != null
            ? paymentMapper.toJpa(domain.getPayment())
            : null;

    return JpaOrderEntity.fromDomain (
            domain,
            jpaUserEntity,
            jpaOrderItemEntityList,
            jpaPaymentEntity
    );
  }

  public Order toDomain(JpaOrderEntity jpa) {
    if (jpa == null) return null;

    User domainUser = jpa.getUser() != null
            ? userMapper.toDomain(jpa.getUser())
            : null;

    List<OrderItem> domainItems = jpa.getItems() != null
            ? jpa.getItems().stream().map(orderItemMapper::toDomain).toList()
            : null;

    Payment domainPayment = jpa.getPayment() != null
            ? paymentMapper.toDomain(jpa.getPayment())
            : null;

    Order order = new Order();
    order.setId(jpa.getId());
    order.setStatus(jpa.getStatus());
    order.setOrderDate(jpa.getOrderDate());
    order.setTotalValue(jpa.getTotalValue());
    order.setUser(domainUser);
    order.setItems(domainItems);
    order.setPayment(domainPayment);

    return order;
  }
}
