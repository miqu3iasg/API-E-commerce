package com.application.API_E_commerce.adapters.outbound.repositories;

import com.application.API_E_commerce.adapters.outbound.entities.category.JpaCategoryEntity;
import com.application.API_E_commerce.adapters.outbound.entities.product.JpaProductEntity;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.dtos.ProductFiltersCriteria;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import com.application.API_E_commerce.utils.converters.CategoryConverter;
import com.application.API_E_commerce.utils.converters.ProductConverter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductRepositoryImplementation implements ProductRepository {
  private final JpaProductRepository jpaProductRepository;
  private final ProductConverter productConverter;

  public ProductRepositoryImplementation(
          JpaProductRepository jpaProductRepository,
          ProductConverter productConverter
  ) {
    this.jpaProductRepository = jpaProductRepository;
    this.productConverter = productConverter;
  }

  @Override
  @Transactional
  public Product saveProduct(Product product) {
    JpaProductEntity productEntityToSave = this.productConverter.toJpa(product);
    JpaProductEntity productEntityToConvert = this.jpaProductRepository.save(productEntityToSave);
    return productConverter.toDomain(productEntityToConvert);
  }

  @Override
  public Optional<Product> findProductById(UUID productId) {
    return Optional.ofNullable(jpaProductRepository.findById(productId)
            .map(productConverter::toDomain)
            .orElseThrow(() -> new IllegalArgumentException("Product was not found when searching for id in the repository.")));
  }

  @Override
  public void deleteProduct(Product product) {
    JpaProductEntity productEntityToDelete = this.productConverter.toJpa(product);

    this.jpaProductRepository.findById(product.getId())
            .map(existingProductEntity -> {
              this.jpaProductRepository.delete(existingProductEntity);
              return existingProductEntity;
            }).orElseThrow(() -> new IllegalArgumentException("Product was not found when searching for id in the delete product method."));
  }

  @Override
  public void deleteProductById(UUID productId) {
    this.jpaProductRepository.findById(productId)
            .map(existingProductEntity -> {
              this.jpaProductRepository.deleteById(productId);
              return existingProductEntity;
            }).orElseThrow(() -> new IllegalArgumentException("Product was not found when searching for id in the delete product by id method."));
  }

  @Override
  public List<Product> filterProducts(ProductFiltersCriteria request) {
    List<JpaProductEntity> jpaProductEntityFilteredList = this.jpaProductRepository.filterProducts(
            request.getName(),
            request.getDescription(),
            request.getMinPrice(),
            request.getMaxPrice(),
            request.getCategoryId()
    );

    if (jpaProductEntityFilteredList.isEmpty()) return Collections.emptyList();

    return jpaProductEntityFilteredList.stream().map(productConverter::toDomain).toList();
  }

  @Override
  public List<Product> findAllProducts() {
    List<JpaProductEntity> jpaProductEntityList = this.jpaProductRepository.findAll();

    if (jpaProductEntityList.isEmpty()) return Collections.emptyList();

    return jpaProductEntityList.stream().map(productConverter::toDomain).toList();
  }
}
