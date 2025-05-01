package com.application.API_E_commerce.domain.product.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductResponseDTO {

  private UUID uuid;
  private String name;
  private String description;
  private BigDecimal price;
  private int stock;
  private LocalDateTime createdAt;

  public ProductResponseDTO ( UUID uuid, String name, String description, BigDecimal price, int stock, LocalDateTime createdAt ) {
    this.uuid = uuid;
    this.name = name;
    this.description = description;
    this.price = price;
    this.stock = stock;
    this.createdAt = createdAt;
  }

  public UUID getUuid () {
    return uuid;
  }

  public void setUuid ( UUID uuid ) {
    this.uuid = uuid;
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

  public LocalDateTime getCreatedAt () {
    return createdAt;
  }

  public void setCreatedAt ( LocalDateTime createdAt ) {
    this.createdAt = createdAt;
  }

}
