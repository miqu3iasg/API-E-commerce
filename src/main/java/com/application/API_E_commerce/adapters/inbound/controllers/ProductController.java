package com.application.API_E_commerce.adapters.inbound.controllers;

import com.application.API_E_commerce.adapters.inbound.dtos.CreateProductRequestDTO;
import com.application.API_E_commerce.adapters.inbound.dtos.ProductFiltersCriteria;
import com.application.API_E_commerce.adapters.inbound.dtos.ProductResponseDTO;
import com.application.API_E_commerce.adapters.inbound.dtos.UpdateProductRequestDTO;
import com.application.API_E_commerce.common.response.ApiResponse;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.useCase.ProductUseCase;
import com.application.API_E_commerce.infrastructure.exceptions.product.ProductImageNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product", description = "Operations pertaining to product management")
public class ProductController {

	private final ProductUseCase productService;

	public ProductController (ProductUseCase productService) {
		this.productService = productService;
	}

	@Operation(
			summary = "Create a new product",
			description = "Creates a product with the provided data"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "201",
					description = "Product created successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid product request data",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@PostMapping
	public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct (
			@Parameter(description = "Product data to create", required = true)
			@Valid @RequestBody CreateProductRequestDTO request) {
		Product product = productService.createProduct(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Product created successfully", toResponse(product), HttpStatus.CREATED));
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

	@Operation(
			summary = "Update a product",
			description = "Updates the product data for the specified product ID",
			tags = {"Product update"}
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Product updated successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid product request data",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "Product not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@PutMapping("/{productId}")
	public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct (
			@Parameter(description = "Product ID to update", required = true)
			@PathVariable UUID productId,
			@Parameter(description = "Product data to update", required = true)
			@Valid @RequestBody UpdateProductRequestDTO request) {
		Product product = productService.updateProduct(productId, request);
		return ResponseEntity.ok(ApiResponse.success("Product updated successfully", toResponse(product), HttpStatus.OK));
	}

	@Operation(
			summary = "Delete a product",
			description = "Deletes a product using the specified product ID"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "204",
					description = "Product deleted successfully (no content)"
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "Product not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@DeleteMapping("/{productId}")
	public ResponseEntity<Void> deleteProduct (
			@Parameter(description = "Product ID to delete", required = true)
			@PathVariable UUID productId) {
		productService.deleteProduct(productId);
		return ResponseEntity.noContent().build();
	}

	@Operation(
			summary = "Retrieve all products",
			description = "Returns a list of all registered products"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Products retrieved successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class),
							array = @ArraySchema(schema = @Schema(implementation = ProductResponseDTO.class))
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@GetMapping
	public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAllProducts () {
		List<ProductResponseDTO> products = productService.findAllProducts()
				.stream()
				.map(ProductController::toResponse)
				.toList();
		return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", products, HttpStatus.OK));
	}

	@Operation(
			summary = "Filter products",
			description = "Filters products based on the provided criteria. Complex filters should be sent in the request body."
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Products filtered successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class),
							array = @ArraySchema(schema = @Schema(implementation = ProductResponseDTO.class))
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid filter criteria",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@GetMapping("/filter")
	public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> filterProducts (
			@Parameter(description = "Criteria to filter products", required = true)
			@RequestBody ProductFiltersCriteria criteria) {
		List<ProductResponseDTO> products = productService.filterProducts(criteria)
				.stream()
				.map(ProductController::toResponse)
				.toList();
		return ResponseEntity.ok(ApiResponse.success("Products filtered successfully", products, HttpStatus.OK));
	}

	@Operation(
			summary = "Associate product to category",
			description = "Associates a product to a specified category",
			tags = {"Product category"}
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Product associated to category successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "Product or category not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@PutMapping("/{productId}/category/{categoryId}")
	public ResponseEntity<ApiResponse<ProductResponseDTO>> addCategory (
			@Parameter(description = "Product ID to associate", required = true)
			@PathVariable UUID productId,
			@Parameter(description = "Category ID to associate", required = true)
			@PathVariable UUID categoryId) {
		Product product = productService.associateProductToCategory(productId, categoryId);
		return ResponseEntity.ok(ApiResponse.success("Product associated to category successfully", toResponse(product), HttpStatus.OK));
	}

	@Operation(
			summary = "Remove product from category",
			description = "Removes the category association from a product",
			tags = {"Product category"}
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Product removed from category successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "Product not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@DeleteMapping("/{productId}/category")
	public ResponseEntity<ApiResponse<ProductResponseDTO>> removeCategory (
			@Parameter(description = "Product ID to remove from category", required = true)
			@PathVariable UUID productId) {
		Product product = productService.removeProductFromCategory(productId);
		return ResponseEntity.ok(ApiResponse.success("Product removed from category successfully", toResponse(product), HttpStatus.OK));
	}

	@Operation(
			summary = "Retrieve product category",
			description = "Retrieves the category associated with a product"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Product category retrieved successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "Product or category not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@GetMapping("/{productId}/category")
	public ResponseEntity<ApiResponse<Category>> getProductCategory (
			@Parameter(description = "Product ID to retrieve category", required = true)
			@PathVariable UUID productId) {
		Category category = productService.getProductCategory(productId);
		return ResponseEntity.ok(ApiResponse.success("Product category retrieved successfully", category, HttpStatus.OK));
	}

	@Operation(
			summary = "Upload product images",
			description = "Uploads images for a specified product"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Product images uploaded successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid image data or format",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "Product not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@PostMapping("/{productId}/images")
	public ResponseEntity<ApiResponse<Void>> uploadImages (
			@Parameter(description = "Product ID to upload images", required = true)
			@PathVariable UUID productId,
			@Parameter(description = "List of image URLs to upload", required = true)
			@RequestBody List<String> imagesUrl) throws IOException {
		productService.uploadProductImage(productId, imagesUrl);
		return ResponseEntity.ok(ApiResponse.success("Product images uploaded successfully", null, HttpStatus.OK));
	}

	@Operation(
			summary = "Delete product images",
			description = "Deletes all images associated with a product"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Product images deleted successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "Product or images not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@DeleteMapping("/{productId}/images")
	public ResponseEntity<ApiResponse<Void>> deleteImages (
			@Parameter(description = "Product ID to delete images", required = true)
			@PathVariable UUID productId) throws IOException, ProductImageNotFoundException {
		productService.deleteProductImage(productId);
		return ResponseEntity.ok(ApiResponse.success("Product images deleted successfully", null, HttpStatus.OK));
	}

	@Operation(
			summary = "Retrieve product images",
			description = "Retrieves all images associated with a product"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Product images retrieved successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class),
							array = @ArraySchema(schema = @Schema(implementation = String.class))
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "Product not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@GetMapping("/{productId}/images")
	public ResponseEntity<ApiResponse<List<String>>> getImages (
			@Parameter(description = "Product ID to retrieve images", required = true)
			@PathVariable UUID productId) {
		List<String> images = productService.getProductImages(productId);
		return ResponseEntity.ok(ApiResponse.success("Product images retrieved successfully", images, HttpStatus.OK));
	}

}