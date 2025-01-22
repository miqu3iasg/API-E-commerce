package com.application.API_E_commerce.adapters.outbound.entities.product;

import com.application.API_E_commerce.adapters.outbound.entities.category.JpaCategoryEntity;
import com.application.API_E_commerce.domain.product.Product;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_products")
@AllArgsConstructor
@NoArgsConstructor
public class  JpaProductEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private String name;

  private String description;

  private BigDecimal price;

  private int stock;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "category_id", nullable = true)
  private JpaCategoryEntity category;

  private String imageUrl;

  private LocalDateTime createdAt;

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

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public int getStock() {
    return stock;
  }

  public void setStock(int stock) {
    this.stock = stock;
  }

  public JpaCategoryEntity getCategory() {
    return category;
  }

  public void setCategory(JpaCategoryEntity category) {
    this.category = category;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
  }

  @Transactional
  public static JpaProductEntity fromDomain(Product productDomain, JpaCategoryEntity jpaCategoryEntity) {
    JpaProductEntity jpaProductEntity = new JpaProductEntity();

    if (productDomain.getId() != null) {
      jpaProductEntity.id = productDomain.getId();
    }

    jpaProductEntity.name = productDomain.getName();
    jpaProductEntity.description = productDomain.getDescription();
    jpaProductEntity.price = productDomain.getPrice();
    jpaProductEntity.stock = productDomain.getStock();
    jpaProductEntity.category = jpaCategoryEntity;
    jpaProductEntity.imageUrl = productDomain.getImageUrl();
    jpaProductEntity.createdAt = productDomain.getCreatedAt();

    return jpaProductEntity;
  }
}
