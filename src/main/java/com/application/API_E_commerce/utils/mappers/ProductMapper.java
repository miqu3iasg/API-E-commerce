package com.application.API_E_commerce.utils.mappers;

import com.application.API_E_commerce.adapters.outbound.entities.category.JpaCategoryEntity;
import com.application.API_E_commerce.adapters.outbound.entities.product.JpaProductEntity;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.product.Product;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = { CategoryMapper.class })
public interface ProductMapper {

  ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

  @Mappings({
          @Mapping(source = "id", target = "id"),
          @Mapping(source = "name", target = "name"),
          @Mapping(source = "description", target = "description"),
          @Mapping(source = "price", target = "price"),
          @Mapping(source = "stock", target = "stock"),
          @Mapping(source = "category", target = "category", qualifiedByName = "categoryToJpa"),
          @Mapping(source = "imageUrl", target = "imageUrl"),
          @Mapping(source = "createdAt", target = "createdAt"),
  })
  JpaProductEntity toJpa(Product product);

  @Mappings({
          @Mapping(source = "id", target = "id"),
          @Mapping(source = "name", target = "name"),
          @Mapping(source = "description", target = "description"),
          @Mapping(source = "price", target = "price"),
          @Mapping(source = "stock", target = "stock"),
          @Mapping(source = "category", target = "category", qualifiedByName = "categoryToDomain"),
          @Mapping(source = "imageUrl", target = "imageUrl"),
          @Mapping(source = "createdAt", target = "createdAt"),
  })
  Product toDomain(JpaProductEntity jpa);

  @Named("productToJpa")
  default JpaProductEntity productToJpa(Product product) {
    return product == null ? null : ProductMapper.INSTANCE.toJpa(product);
  }

  @Named("productToDomain")
  default Product productToDomain(JpaProductEntity jpaProduct) {
    return jpaProduct == null ? null : ProductMapper.INSTANCE.toDomain(jpaProduct);
  }

  @Named("categoryToJpa")
  default JpaCategoryEntity categoryToJpa(Category category) {
    return category == null ? null : CategoryMapper.INSTANCE.toJpa(category);
  }

  @Named("categoryToDomain")
  default Category categoryToDomain(JpaCategoryEntity jpaCategory) {
    return jpaCategory == null ? null : CategoryMapper.INSTANCE.toDomain(jpaCategory);
  }
}
