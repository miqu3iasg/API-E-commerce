package com.application.API_E_commerce.adapters.outbound.entities.order;

import com.application.API_E_commerce.adapters.outbound.entities.product.JpaProductEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tb_order_items")
@AllArgsConstructor
@NoArgsConstructor
public class JpaOrderItemEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "order_id", nullable = false)
  private JpaOrderEntity order;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private JpaProductEntity product;

  private int quantity;

  private BigDecimal unitPrice;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public JpaOrderEntity getOrder() {
    return order;
  }

  public void setOrder(JpaOrderEntity order) {
    this.order = order;
  }

  public JpaProductEntity getProduct() {
    return product;
  }

  public void setProduct(JpaProductEntity product) {
    this.product = product;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }
}
