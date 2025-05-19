package com.application.API_E_commerce.domain.product.repository;

import com.application.API_E_commerce.adapters.inbound.dtos.ProductFiltersCriteria;
import com.application.API_E_commerce.domain.product.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {

	Product saveProduct (Product product);

	Optional<Product> findProductById (UUID productId);

	void deleteProduct (Product product);

	void deleteProductById (UUID productId);

	List<Product> findAllProducts ();

	List<Product> filterProducts (ProductFiltersCriteria criteria);

}
