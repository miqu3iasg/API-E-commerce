package com.application.API_E_commerce.adapters.outbound.entities.category;

import com.application.API_E_commerce.adapters.outbound.entities.product.JpaProductEntity;
import com.application.API_E_commerce.domain.category.Category;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_categories")
@AllArgsConstructor
@NoArgsConstructor
public class JpaCategoryEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private String name;

  private String description;

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JpaProductEntity> products = new ArrayList<>();

  @Version
  private long version;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<JpaProductEntity> getProducts() {
    return products;
  }

  public void setProducts(List<JpaProductEntity> products) {
    this.products = products;
  }

  @Transactional
  public static JpaCategoryEntity fromDomain(
          Category categoryDomain,
          List<JpaProductEntity> jpaProductEntityList
  ) {
    JpaCategoryEntity jpaCategoryEntity = new JpaCategoryEntity();

    if (categoryDomain.getId() != null) {
      jpaCategoryEntity.setId(categoryDomain.getId());
    }

    jpaCategoryEntity.name = categoryDomain.getName();
    jpaCategoryEntity.description = categoryDomain.getDescription();
    jpaCategoryEntity.products = jpaProductEntityList;

    return jpaCategoryEntity;
  }
}
