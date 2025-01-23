package com.application.API_E_commerce.domain.category;

import com.application.API_E_commerce.domain.product.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Category {
  private UUID id;
  private String name;
  private String description;
  private List<Product> products = new ArrayList<>();

  public Category() {
  }

  public Category(UUID id, String name, String description, List<Product> products) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.products = products;
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

  public List<Product> getProducts() {
    return products;
  }

  public void setProducts(List<Product> products) {
    this.products = products;
  }
}
