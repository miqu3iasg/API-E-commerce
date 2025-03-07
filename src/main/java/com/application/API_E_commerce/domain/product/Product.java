package com.application.API_E_commerce.domain.product;

import com.application.API_E_commerce.domain.category.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class Product {
  private UUID id;
  private String name;
  private String description;
  private BigDecimal price;
  private int stock;
  private Category category;
  private List<String> imagesUrl = new ArrayList<>();
  private LocalDateTime createdAt;
  private long version = 0;

  public Product() {
  }

  public Product(UUID id, String name, String description, BigDecimal price, int stock, Category category, List<String> imagesUrl, LocalDateTime createdAt) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.price = price;
    this.stock = stock;
    this.category = category;
    this.imagesUrl = imagesUrl;
    this.createdAt = createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Product product = (Product) o;
    return Objects.equals(id, product.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

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

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public List<String> getImagesUrl() {
    return imagesUrl == null ? Collections.emptyList() : imagesUrl;
  }

  public void setImagesUrl(List<String> imageUrl) {
    this.imagesUrl = imageUrl;
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
}
