package com.application.API_E_commerce.utils.validators;

import com.application.API_E_commerce.adapters.outbound.entities.product.JpaProductEntity;
import com.application.API_E_commerce.adapters.outbound.repositories.JpaProductRepository;
import com.application.API_E_commerce.adapters.outbound.repositories.ProductRepositoryImplementation;
import com.application.API_E_commerce.application.usecases.CategoryUseCases;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.dtos.CreateProductRequestDTO;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import com.application.API_E_commerce.utils.converters.ProductConverter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductValidator {
  private final ProductRepository productRepository;

  public ProductValidator(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public void validateCreateProductRequest(CreateProductRequestDTO request) {
    if (request.price() == null || request.price().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("The price should be greater than zero.");
    }

    if (request.stock() < 0) {
      throw new IllegalArgumentException("Stock cannot be smaller than zero.");
    }
  }

  public Product validateIfProductExistsAndReturnTheExistingProduct(UUID productId) {
    Optional<Product> productOptional = this.productRepository.findProductById(productId);

    if (productOptional.isEmpty()) {
      throw new IllegalArgumentException("Product was not found.");
    }

    return productOptional.get();
  }
}
