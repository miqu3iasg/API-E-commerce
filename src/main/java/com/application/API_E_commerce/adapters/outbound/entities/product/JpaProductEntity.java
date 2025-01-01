package com.application.API_E_commerce.adapters.outbound.entities.product;

import com.application.API_E_commerce.adapters.outbound.entities.category.JpaCategoryEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JpaProductEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private String name;

  private String description;

  private BigDecimal price;

  private int stock;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private JpaCategoryEntity category;

  private String imageUrl;

  private LocalDateTime createdAt;
}
