package com.application.API_E_commerce.factory;

import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.product.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductFactory {
  public static Product build() {
    Product product = new Product();
    product.setId(UUID.randomUUID());
    product.setName("Test Product");
    product.setDescription("Test description");
    product.setPrice(new BigDecimal("0.0"));
    product.setStock(100);
    product.setCategory(null);
    product.setVersion(0);
    product.setCreatedAt(LocalDateTime.now());

    return product;
  }
}
