package com.application.API_E_commerce.application.usecases;

import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.dtos.CreateProductRequestDTO;
import com.application.API_E_commerce.domain.product.dtos.ProductFiltersCriteria;
import com.application.API_E_commerce.domain.product.dtos.UpdateProductRequestDTO;
import com.application.API_E_commerce.infrastructure.exceptions.product.ProductImageNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductUseCases {

	Product createProduct (CreateProductRequestDTO createProductRequest);

	Optional<Product> findProductById (UUID productId);

	List<Product> findAllProducts ();

	Product updateProduct (UUID productId, UpdateProductRequestDTO updatedProductRequest);

	void deleteProduct (UUID productId);

	Product associateProductToCategory (UUID productId, UUID categoryId);

	Category getProductCategory (UUID productId);

	Product removeProductFromCategory (UUID productId);

	void uploadProductImage (UUID productId, List<String> imagesUrl) throws IOException;

	List<String> getProductImages (UUID productId);

	void deleteProductImage (UUID productId) throws IOException, ProductImageNotFoundException;

	int getProductStock (UUID productId);

	List<Product> filterProducts (ProductFiltersCriteria criteria);

}
