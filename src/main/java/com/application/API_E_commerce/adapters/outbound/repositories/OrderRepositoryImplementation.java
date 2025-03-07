package com.application.API_E_commerce.adapters.outbound.repositories;

import com.application.API_E_commerce.adapters.outbound.entities.order.JpaOrderEntity;
import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.order.OrderRepository;
import com.application.API_E_commerce.utils.mappers.OrderMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class OrderRepositoryImplementation implements OrderRepository {
  private final JpaOrderRepository jpaOrderRepository;
  private final OrderMapper orderMapper;

  public OrderRepositoryImplementation(JpaOrderRepository jpaOrderRepository, OrderMapper orderMapper) {
    this.jpaOrderRepository = jpaOrderRepository;
    this.orderMapper = orderMapper;
  }

  @Override
  public Order saveOrder(Order order) {
    JpaOrderEntity orderEntityToSave = orderMapper.toJpa(order);

    JpaOrderEntity savedOrderEntity = this.jpaOrderRepository.save(orderEntityToSave);

    return orderMapper.toDomain(savedOrderEntity);
  }

  @Override
  public List<Order> findAllOrders() {
    List<JpaOrderEntity> jpaOrderEntityList = this.jpaOrderRepository.findAll();

    if (jpaOrderEntityList.isEmpty()) return Collections.emptyList();

    return jpaOrderEntityList.stream().map(orderMapper::toDomain).toList();
  }

  @Override
  public Optional<Order> findOrderById(UUID orderId) {
    return Optional.ofNullable(jpaOrderRepository.findById(orderId)
            .map(orderMapper::toDomain)
            .orElseThrow(() -> new IllegalArgumentException("Order was not found when searching for id in the repository.")));
  }

  @Override
  public void deleteOrder(UUID orderId) {
    this.jpaOrderRepository.findById(orderId)
            .map(existingOrderEntity -> {
              this.jpaOrderRepository.deleteById(orderId);
              return existingOrderEntity;
            }).orElseThrow(() -> new IllegalArgumentException("Order was not found when searching for id in the delete product by id method."));
  }
}
