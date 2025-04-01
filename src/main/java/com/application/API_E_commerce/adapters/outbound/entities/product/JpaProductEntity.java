package com.application.API_E_commerce.adapters.outbound.entities.product;

import com.application.API_E_commerce.adapters.outbound.entities.cart.JpaCartItemEntity;
import com.application.API_E_commerce.adapters.outbound.entities.category.JpaCategoryEntity;
import com.application.API_E_commerce.domain.product.Product;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_products")
@AllArgsConstructor
@NoArgsConstructor
public class JpaProductEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private String name;

  private String description;

  private BigDecimal price;

  private int stock;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "category_id", nullable = true)
  private JpaCategoryEntity category;

  @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = true)
  private List<JpaCartItemEntity> items = new ArrayList<>();

  private List<String> imagesUrl = new ArrayList<>();

  private LocalDateTime createdAt;

  @Version
  private long version;

  @PrePersist
  public void prePersist () {
    if ( this.id == null ) {
      this.id = UUID.randomUUID();
    }
  }

  public UUID getId () {
    return id;
  }

  public void setId ( UUID id ) {
    this.id = id;
  }

  public String getName () {
    return name;
  }

  public void setName ( String name ) {
    this.name = name;
  }

  public String getDescription () {
    return description;
  }

  public void setDescription ( String description ) {
    this.description = description;
  }

  public BigDecimal getPrice () {
    return price;
  }

  public void setPrice ( BigDecimal price ) {
    this.price = price;
  }

  public int getStock () {
    return stock;
  }

  public void setStock ( int stock ) {
    this.stock = stock;
  }

  public JpaCategoryEntity getCategory () {
    return category;
  }

  public void setCategory ( JpaCategoryEntity category ) {
    this.category = category;
  }

  public List<String> getImagesUrl () {
    return imagesUrl == null ? Collections.emptyList() : imagesUrl;
  }

  public void setImagesUrl ( List<String> imagesUrl ) {
    this.imagesUrl = imagesUrl;
  }

  public LocalDateTime getCreatedAt () {
    return createdAt;
  }

  public void setCreatedAt ( LocalDateTime createdAt ) {
    this.createdAt = createdAt;
  }

  public long getVersion () {
    return version;
  }

  public void setVersion ( long version ) {
    this.version = version;
  }

  public List<JpaCartItemEntity> getItems () {
    return items;
  }

  public void setItems ( List<JpaCartItemEntity> items ) {
    this.items = items;
  }

  @Transactional
  public static JpaProductEntity fromDomain ( Product productDomain, JpaCategoryEntity jpaCategoryEntity ) {
    JpaProductEntity jpaProductEntity = new JpaProductEntity();

    if ( productDomain.getId() != null ) {
      jpaProductEntity.id = productDomain.getId();
    }

    jpaProductEntity.name = productDomain.getName();
    jpaProductEntity.description = productDomain.getDescription();
    jpaProductEntity.price = productDomain.getPrice();
    jpaProductEntity.stock = productDomain.getStock();
    jpaProductEntity.category = jpaCategoryEntity;
    jpaProductEntity.imagesUrl = productDomain.getImagesUrl();
    jpaProductEntity.createdAt = productDomain.getCreatedAt();
    jpaProductEntity.version = productDomain.getVersion();

    return jpaProductEntity;
  }

}
