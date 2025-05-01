package com.application.API_E_commerce.adapters.inbound.controllers;

import com.application.API_E_commerce.application.usecases.ProductUseCases;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.dtos.CreateProductRequestDTO;
import com.application.API_E_commerce.domain.product.dtos.ProductFiltersCriteria;
import com.application.API_E_commerce.domain.product.dtos.ProductResponseDTO;
import com.application.API_E_commerce.domain.product.dtos.UpdateProductRequestDTO;
import com.application.API_E_commerce.infrastructure.exceptions.product.ProductImageNotFoundException;
import com.application.API_E_commerce.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

	private final ProductUseCases productService;

	public ProductController (ProductUseCases productService) {
		this.productService = productService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct (@RequestBody CreateProductRequestDTO request) {
		Product product = productService.createProduct(request);

		return ResponseEntity.ok(ApiResponse.success("Product created successfully", toResponse(product), HttpStatus.CREATED));
	}

	private static ProductResponseDTO toResponse (Product product) {
		return new ProductResponseDTO(
				product.getId(),
				product.getName(),
				product.getDescription(),
				product.getPrice(),
				product.getStock(),
				product.getCreatedAt()
		);
	}

	@PutMapping("/{productId}")
	public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct (@PathVariable UUID productId, @RequestBody UpdateProductRequestDTO request) {
		Product product = productService.updateProduct(productId, request);

		return ResponseEntity.ok(ApiResponse.success("Product updated successfully", toResponse(product), HttpStatus.OK));
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<ApiResponse<Void>> deleteProduct (@PathVariable UUID productId) {
		productService.deleteProduct(productId);

		return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null, HttpStatus.OK));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAllProducts () {
		List<ProductResponseDTO> products = productService.findAllProducts().stream().map(ProductController::toResponse).toList();

		return ResponseEntity.ok(ApiResponse.success("Products fetched successfully", products, HttpStatus.OK));
	}

	@GetMapping("/filter")
	public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> filterProducts (@RequestBody ProductFiltersCriteria criteria) {
		List<ProductResponseDTO> products = productService.filterProducts(criteria).stream().map(ProductController::toResponse).toList();

		return ResponseEntity.ok(ApiResponse.success("Products filtered successfully", products, HttpStatus.OK));
	}

	@PutMapping("/{productId}/category/{categoryId}")
	public ResponseEntity<ApiResponse<ProductResponseDTO>> addCategory (@PathVariable UUID productId, @PathVariable UUID categoryId) {
		Product product = productService.associateProductToCategory(productId, categoryId);

		return ResponseEntity.ok(ApiResponse.success("Product associated to category successfully", toResponse(product), HttpStatus.OK));
	}

	@DeleteMapping("/{productId}/category")
	public ResponseEntity<ApiResponse<ProductResponseDTO>> removeCategory (@PathVariable UUID productId) {
		Product product = productService.removeProductFromCategory(productId);

		return ResponseEntity.ok(ApiResponse.success("Product removed from category successfully", toResponse(product), HttpStatus.OK));
	}

	@GetMapping("/{productId}/category")
	public ResponseEntity<ApiResponse<Category>> getProductCategory (@PathVariable UUID productId) {
		Category category = productService.getProductCategory(productId);

		return ResponseEntity.ok(ApiResponse.success("Product category fetched successfully", category, HttpStatus.OK));
	}

	@PostMapping("/{productId}/images")
	public ResponseEntity<ApiResponse<Void>> uploadImages (@PathVariable UUID productId, @RequestBody List<String> imagesUrl) throws IOException {
		productService.uploadProductImage(productId, imagesUrl);

		return ResponseEntity.ok(ApiResponse.success("Product images uploaded successfully", null, HttpStatus.OK));
	}

	@DeleteMapping("/{productId}/images")
	public ResponseEntity<ApiResponse<Void>> deleteImages (@PathVariable UUID productId) throws IOException, ProductImageNotFoundException {
		productService.deleteProductImage(productId);

		return ResponseEntity.ok(ApiResponse.success("Product images removed successfully", null, HttpStatus.OK));
	}

	@GetMapping("/{productId}/images")
	public ResponseEntity<ApiResponse<List<String>>> getImages (@PathVariable UUID productId) {
		List<String> images = productService.getProductImages(productId);

		return ResponseEntity.ok(ApiResponse.success("Product images fetched successfully", images, HttpStatus.OK));
	}

}