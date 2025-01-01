package com.application.API_E_commerce.adapters.outbound.entities;

import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "tb_cart_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JpaCartItem {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "cart_id", nullable = false)
  private Cart cart;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  private int quantity;
}
