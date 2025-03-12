package com.application.API_E_commerce.adapters.outbound.entities.cart;

import com.application.API_E_commerce.adapters.outbound.entities.product.JpaProductEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "tb_cart_items")
@AllArgsConstructor
@NoArgsConstructor
public class JpaCartItemEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "cart_id", nullable = false)
  private JpaCartEntity cart;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private JpaProductEntity product;

  private int quantity;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public JpaCartEntity getCart() {
    return cart;
  }

  public void setCart(JpaCartEntity cart) {
    this.cart = cart;
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
}
