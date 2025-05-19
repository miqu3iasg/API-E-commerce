package com.application.API_E_commerce.common.utils.validators;

import com.application.API_E_commerce.adapters.inbound.dtos.CreateProductRequestDTO;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.repository.ProductRepositoryPort;
import com.application.API_E_commerce.infrastructure.exceptions.product.InvalidPriceException;
import com.application.API_E_commerce.infrastructure.exceptions.product.InvalidQuantityException;
import com.application.API_E_commerce.infrastructure.exceptions.product.ProductNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductValidator {

	private final ProductRepositoryPort productRepositoryPort;

	public ProductValidator (ProductRepositoryPort productRepositoryPort) {
		this.productRepositoryPort = productRepositoryPort;
	}

	public void validateCreateProductRequest (CreateProductRequestDTO request) {
		if (request.price() == null || request.price().compareTo(BigDecimal.ZERO) <= 0)
			throw new InvalidPriceException("The price should be greater than zero.");

		if (request.stock() < 0)
			throw new InvalidQuantityException("Stock cannot be smaller than zero.");
	}

	public Product validateIfProductExistsAndReturnTheExistingProduct (UUID productId) {
		Optional<Product> productOptional = productRepositoryPort.findProductById(productId);

		if (productOptional.isEmpty())
			throw new ProductNotFoundException("Product was not found.");

		return productOptional.get();
	}

}
