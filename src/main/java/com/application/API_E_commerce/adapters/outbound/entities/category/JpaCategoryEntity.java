package com.application.API_E_commerce.adapters.outbound.entities.category;

import com.application.API_E_commerce.adapters.outbound.entities.product.JpaProductEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JpaCategoryEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private String name;

  private String description;

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JpaProductEntity> products;
}
