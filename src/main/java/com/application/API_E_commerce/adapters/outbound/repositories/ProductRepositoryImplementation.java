package com.application.API_E_commerce.adapters.outbound.repositories;

import com.application.API_E_commerce.adapters.outbound.entities.category.JpaCategoryEntity;
import com.application.API_E_commerce.adapters.outbound.entities.product.JpaProductEntity;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.dtos.ProductFiltersCriteria;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import com.application.API_E_commerce.utils.mappers.CategoryMapper;
import com.application.API_E_commerce.utils.mappers.ProductMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductRepositoryImplementation implements ProductRepository {
  private final JpaProductRepository jpaProductRepository;
  private final ProductMapper productMapper;
  private final CategoryMapper categoryMapper;

  public ProductRepositoryImplementation(
          JpaProductRepository jpaProductRepository,
          ProductMapper productMapper,
          CategoryMapper categoryMapper
  ) {
    this.jpaProductRepository = jpaProductRepository;
    this.productMapper = productMapper;
    this.categoryMapper = categoryMapper;
  }

  @Override
  @Transactional
  public Product saveProduct(Product product) {
    JpaProductEntity productEntityToSave;

    if (product.getId() != null) {
      JpaProductEntity existingEntity = this.jpaProductRepository.findById(product.getId()).orElseThrow();
      productEntityToSave = updateExistingEntity(existingEntity, product);
      this.jpaProductRepository.save(productEntityToSave);
    }

    productEntityToSave = this.productMapper.toJpa(product);

    JpaProductEntity savedProduct = this.jpaProductRepository.save(productEntityToSave);

    return productMapper.toDomain(savedProduct);
  }

  private JpaProductEntity updateExistingEntity(JpaProductEntity existingEntity, Product product) {
    existingEntity.setName(product.getName());
    existingEntity.setDescription(product.getDescription());
    existingEntity.setPrice(product.getPrice());
    existingEntity.setStock(product.getStock());
    existingEntity.setImagesUrl(product.getImagesUrl());
    existingEntity.setCreatedAt(product.getCreatedAt());
    existingEntity.setVersion(product.getVersion());

    if (product.getCategory() != null) {
      JpaCategoryEntity existingCategory = existingEntity.getCategory();

      if (existingCategory != null && existingCategory.getId().equals(product.getCategory().getId())) {
        existingEntity.setCategory(existingCategory);
      } else {
        existingEntity.setCategory(categoryMapper.toJpa(product.getCategory()));
      }
    } else {
      existingEntity.setCategory(null);
    }

    return existingEntity;
  }

  @Override
  @Transactional
  public Optional<Product> findProductById(UUID productId) {
    return Optional.ofNullable(jpaProductRepository.findById(productId)
            .map(productMapper::toDomain)
            .orElseThrow(() -> new IllegalArgumentException("Product was not found when searching for id in the repository.")));
  }

  @Override
  public void deleteProduct(Product product) {
    JpaProductEntity productEntityToDelete = this.productMapper.toJpa(product);

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

    return jpaProductEntityFilteredList.stream().map(productMapper::toDomain).toList();
  }

  @Override
  public List<Product> findAllProducts() {
    List<JpaProductEntity> jpaProductEntityList = this.jpaProductRepository.findAll();

    if (jpaProductEntityList.isEmpty()) return Collections.emptyList();

    return jpaProductEntityList.stream().map(productMapper::toDomain).toList();
  }
}
