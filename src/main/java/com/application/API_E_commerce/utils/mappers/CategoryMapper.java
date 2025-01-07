package com.application.API_E_commerce.utils.mappers;

import com.application.API_E_commerce.adapters.outbound.entities.category.JpaCategoryEntity;
import com.application.API_E_commerce.adapters.outbound.entities.product.JpaProductEntity;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.product.Product;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface CategoryMapper {

  CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

  @Mappings({
          @Mapping(source = "id", target = "id"),
          @Mapping(source = "name", target = "name"),
          @Mapping(source = "description", target = "description"),
          @Mapping(source = "products", target = "products", qualifiedByName = "productsToJpa")
  })
  JpaCategoryEntity toJpa(Category domain);

  @Mappings({
          @Mapping(source = "id", target = "id"),
          @Mapping(source = "name", target = "name"),
          @Mapping(source = "description", target = "description"),
          @Mapping(source = "products", target = "products", qualifiedByName = "productsToDomain")
  })
  Category toDomain(JpaCategoryEntity jpa);

  @Named("productsToJpa")
  default List<JpaProductEntity> productsToJpa(List<Product> products) {
    return products == null
            ? null
            : products.stream().map(ProductMapper.INSTANCE::toJpa).toList();
  }

  @Named("productsToDomain")
  default List<Product> productsToDomain(List<JpaProductEntity> jpaProducts) {
    return jpaProducts == null
            ? null
            : jpaProducts.stream().map(ProductMapper.INSTANCE::toDomain).toList();
  }
}
