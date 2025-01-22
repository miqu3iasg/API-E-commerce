package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.product.dtos.CreateProductRequestDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ProductValidator {
  public void validate(CreateProductRequestDTO request, Optional<Category> category) {
    if (category.isEmpty()) {
      throw new IllegalArgumentException("Category cannot be null.");
    }
    if (request.price() == null || request.price().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("The price should be greater than zero.");
    }
    if (request.stock() < 0) {
      throw new IllegalArgumentException("Stock cannot be smaller than zero.");
    }
  }
}
