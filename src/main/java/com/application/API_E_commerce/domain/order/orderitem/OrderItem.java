package com.application.API_E_commerce.domain.order.orderitem;

import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.product.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItem {
  private UUID id;
  private Order order;
  private Product product;
  private int quantity;
  private BigDecimal unitPrice;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
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
