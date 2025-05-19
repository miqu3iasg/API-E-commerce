package com.application.API_E_commerce.common.utils.mappers;

import com.application.API_E_commerce.adapters.outbound.entities.cart.JpaCartItemEntity;
import com.application.API_E_commerce.adapters.outbound.entities.category.JpaCategoryEntity;
import com.application.API_E_commerce.adapters.outbound.entities.product.JpaProductEntity;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

	ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

	@Named("cartItemsToJpa")
	default List<JpaCartItemEntity> cartItemsToJpa (Collection<CartItem> items) {
		return items == null ? null : items.stream().map(CartItemMapper.INSTANCE::toJpa).collect(Collectors.toList());
	}

	@Named("cartItemsToDomain")
	default List<CartItem> cartItemsToDomain (Collection<JpaCartItemEntity> items) {
		return items == null ? null : items.stream().map(CartItemMapper.INSTANCE::toDomain).collect(Collectors.toList());
	}

	@Named("productToJpa")
	default JpaProductEntity productToJpa (Product product) {
		return product == null ? null : ProductMapper.INSTANCE.toJpa(product);
	}

	@Mappings({
			@Mapping(source = "id", target = "id"),
			@Mapping(source = "name", target = "name"),
			@Mapping(source = "description", target = "description"),
			@Mapping(source = "price", target = "price"),
			@Mapping(source = "stock", target = "stock"),
			@Mapping(source = "category", target = "category", qualifiedByName = "categoryToJpa"),
			@Mapping(source = "imagesUrl", target = "imagesUrl"),
			@Mapping(source = "items", target = "items", qualifiedByName = "cartItemsToJpa"),
			@Mapping(source = "createdAt", target = "createdAt"),
			@Mapping(source = "version", target = "version")
	})
	JpaProductEntity toJpa (Product product);

	@Named("productToDomain")
	default Product productToDomain (JpaProductEntity jpaProduct) {
		return jpaProduct == null ? null : ProductMapper.INSTANCE.toDomain(jpaProduct);
	}

	@Mappings({
			@Mapping(source = "id", target = "id"),
			@Mapping(source = "name", target = "name"),
			@Mapping(source = "description", target = "description"),
			@Mapping(source = "price", target = "price"),
			@Mapping(source = "stock", target = "stock"),
			@Mapping(source = "category", target = "category", qualifiedByName = "categoryToDomain"),
			@Mapping(source = "imagesUrl", target = "imagesUrl"),
			@Mapping(source = "items", target = "items", qualifiedByName = "cartItemsToDomain"),
			@Mapping(source = "createdAt", target = "createdAt"),
			@Mapping(source = "version", target = "version")
	})
	Product toDomain (JpaProductEntity jpa);

	@Named("categoryToJpa")
	default JpaCategoryEntity categoryToJpa (Category category) {
		if (category == null) return null;

		JpaCategoryEntity jpa = new JpaCategoryEntity();
		jpa.setId(category.getId());
		jpa.setName(category.getName());
		jpa.setDescription(category.getDescription());

		if (category.getProducts().isEmpty() || category.getProducts() == null)
			jpa.setProducts(null);

		List<JpaProductEntity> products = category.getProducts().stream()
				.map(product -> {
					JpaProductEntity jpaProduct = new JpaProductEntity();
					jpaProduct.setId(product.getId());
					jpaProduct.setName(product.getName());
					jpaProduct.setDescription(product.getDescription());
					jpaProduct.setCategory(jpa);
					jpaProduct.setStock(product.getStock());
					jpaProduct.setPrice(product.getPrice());
					jpaProduct.setImagesUrl(product.getImagesUrl());
					jpaProduct.setVersion(product.getVersion());
					jpaProduct.setCreatedAt(product.getCreatedAt());
					return jpaProduct;
				}).collect(Collectors.toList());

		jpa.setProducts(products);
		jpa.setVersion(category.getVersion());

		return jpa;
	}

	@Named("categoryToDomain")
	default Category categoryToDomain (JpaCategoryEntity jpaCategory) {
		if (jpaCategory == null) return null;

		Category domain = new Category();
		domain.setId(jpaCategory.getId());
		domain.setName(jpaCategory.getName());
		domain.setDescription(jpaCategory.getDescription());

		if (jpaCategory.getProducts().isEmpty() || jpaCategory.getProducts() == null)
			domain.setProducts(null);

		List<Product> products = jpaCategory.getProducts().stream()
				.map(product -> {
					Product domainProduct = new Product();
					domainProduct.setId(product.getId());
					domainProduct.setName(product.getName());
					domainProduct.setDescription(product.getDescription());
					domainProduct.setCategory(domain);
					domainProduct.setStock(product.getStock());
					domainProduct.setPrice(product.getPrice());
					domainProduct.setImagesUrl(product.getImagesUrl());
					domainProduct.setVersion(product.getVersion());
					domainProduct.setCreatedAt(product.getCreatedAt());
					return domainProduct;
				}).collect(Collectors.toList());

		domain.setProducts(products);
		domain.setVersion(jpaCategory.getVersion());

		return domain;
	}

}
