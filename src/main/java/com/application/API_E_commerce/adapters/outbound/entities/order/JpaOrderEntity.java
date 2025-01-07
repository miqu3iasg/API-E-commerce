package com.application.API_E_commerce.adapters.outbound.entities.order;

import com.application.API_E_commerce.adapters.outbound.entities.payment.JpaPaymentEntity;
import com.application.API_E_commerce.adapters.outbound.entities.user.JpaUserEntity;
import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.order.OrderStatus;
import com.application.API_E_commerce.utils.mappers.OrderMapper;
import com.application.API_E_commerce.utils.mappers.PaymentMapper;
import com.application.API_E_commerce.utils.mappers.UserMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_orders")
@AllArgsConstructor
@NoArgsConstructor
public class JpaOrderEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private JpaUserEntity user;

  private OrderStatus status;

  private LocalDateTime orderDate;

  private BigDecimal totalValue;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JpaOrderItemEntity> items;

  @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private JpaPaymentEntity payment;

  public static JpaOrderEntity fromDomain (
          Order orderDomain,
          JpaUserEntity jpaUserEntity,
          List<JpaOrderItemEntity> jpaOrderItemEntities,
          JpaPaymentEntity jpaPaymentEntity
  ) {
    JpaOrderEntity jpaOrderEntity = new JpaOrderEntity();
    jpaOrderEntity.id = orderDomain.getId();
    jpaOrderEntity.user = jpaUserEntity;
    jpaOrderEntity.status = orderDomain.getStatus();
    jpaOrderEntity.orderDate = orderDomain.getOrderDate();
    jpaOrderEntity.totalValue = orderDomain.getTotalValue();
    jpaOrderEntity.items = jpaOrderItemEntities;
    jpaOrderEntity.payment = jpaPaymentEntity;

    return jpaOrderEntity;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public JpaUserEntity getUser() {
    return user;
  }

  public void setUser(JpaUserEntity user) {
    this.user = user;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public LocalDateTime getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(LocalDateTime orderDate) {
    this.orderDate = orderDate;
  }

  public BigDecimal getTotalValue() {
    return totalValue;
  }

  public void setTotalValue(BigDecimal totalValue) {
    this.totalValue = totalValue;
  }

  public List<JpaOrderItemEntity> getItems() {
    return items;
  }

  public void setItems(List<JpaOrderItemEntity> items) {
    this.items = items;
  }

  public JpaPaymentEntity getPayment() {
    return payment;
  }

  public void setPayment(JpaPaymentEntity payment) {
    this.payment = payment;
  }
}
