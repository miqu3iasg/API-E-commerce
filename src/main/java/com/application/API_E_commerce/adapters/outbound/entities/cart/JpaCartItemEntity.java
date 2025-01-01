package com.application.API_E_commerce.adapters.outbound.entities.cart;

import com.application.API_E_commerce.adapters.outbound.entities.product.JpaProductEntity;
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
}
