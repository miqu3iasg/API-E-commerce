package com.application.API_E_commerce.utils.converters;

import com.application.API_E_commerce.adapters.outbound.entities.category.JpaCategoryEntity;
import com.application.API_E_commerce.adapters.outbound.entities.product.JpaProductEntity;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.utils.mappers.CategoryMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {
  private final CategoryMapper categoryMapper;

  public ProductConverter(CategoryMapper categoryMapper) {
    this.categoryMapper = categoryMapper;
  }

  @Transactional
  public JpaProductEntity toJpa(Product domain) {
    if (domain == null) return null;

    JpaCategoryEntity jpaCategoryEntity = domain.getCategory() != null
            ? categoryMapper.toJpa(domain.getCategory())
            : null;

    JpaProductEntity jpaProductEntity = new JpaProductEntity();
    jpaProductEntity.setId(domain.getId());
    jpaProductEntity.setName(domain.getName());
    jpaProductEntity.setDescription(domain.getDescription());
    jpaProductEntity.setPrice(domain.getPrice());
    jpaProductEntity.setImageUrl(domain.getImageUrl());
    jpaProductEntity.setCategory(jpaCategoryEntity);
    jpaProductEntity.setStock(domain.getStock());
    jpaProductEntity.setCreatedAt(domain.getCreatedAt());

    return jpaProductEntity;

  }

  @Transactional
  public Product toDomain(JpaProductEntity jpa) {
    if (jpa == null) return null;

    Category category = jpa.getCategory() != null
            ? categoryMapper.toDomain(jpa.getCategory())
            : null;

    return this.productDomainBuilder(jpa, category);
  }

  private Product productDomainBuilder(JpaProductEntity jpaProductEntity, Category category) {
    Product product = new Product();
    product.setId(jpaProductEntity.getId());
    product.setName(jpaProductEntity.getName());
    product.setDescription(jpaProductEntity.getDescription());
    product.setPrice(jpaProductEntity.getPrice());
    product.setStock(jpaProductEntity.getStock());
    product.setCategory(category);
    product.setImageUrl(jpaProductEntity.getImageUrl());
    product.setCreatedAt(jpaProductEntity.getCreatedAt());
    return product;
  }
}
