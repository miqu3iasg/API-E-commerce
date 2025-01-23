package com.application.API_E_commerce.utils.converters;

import com.application.API_E_commerce.adapters.outbound.entities.category.JpaCategoryEntity;
import com.application.API_E_commerce.adapters.outbound.entities.product.JpaProductEntity;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.product.Product;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryConverter {
  private final ProductConverter productConverter;

  public CategoryConverter(ProductConverter productConverter) {
    this.productConverter = productConverter;
  }

  @Transactional
  public JpaCategoryEntity toJpa(Category domain) {
    if (domain == null) return null;

    List<JpaProductEntity> jpaProductEntityList = domain.getProducts() != null
            ? domain.getProducts().stream().map(productConverter::toJpa).toList()
            : null;

    return JpaCategoryEntity.fromDomain(domain, jpaProductEntityList);
  }

  public Category toDomain(JpaCategoryEntity jpa) {
    if (jpa == null) return null;

    List<Product> productList = jpa.getProducts() != null
            ? jpa.getProducts().stream().map(productConverter::toDomain).toList()
            : null;

    return this.categoryDomainBuilder(jpa, productList);
  }

  private Category categoryDomainBuilder(JpaCategoryEntity jpaCategoryEntity, List<Product> products) {
    Category category = new Category();
    category.setName(jpaCategoryEntity.getName());
    category.setDescription(jpaCategoryEntity.getDescription());
    category.setProducts(products);
    return category;
  }
}
