package com.application.API_E_commerce.domain.product.repository;

import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.dtos.ProductFiltersCriteria;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
  Product saveProduct(Product product);
  Optional<Product> findProductById(UUID productId);
  void deleteProduct(Product product);
  void deleteProductById(UUID productId);
  List<Product> findAllProducts();
  List<Product> filterProducts(ProductFiltersCriteria criteria);
}
