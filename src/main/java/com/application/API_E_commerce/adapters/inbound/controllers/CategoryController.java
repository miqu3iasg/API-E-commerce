package com.application.API_E_commerce.adapters.inbound.controllers;

import com.application.API_E_commerce.adapters.inbound.dtos.CategoryResponseDTO;
import com.application.API_E_commerce.adapters.inbound.dtos.CreateCategoryRequestDTO;
import com.application.API_E_commerce.adapters.inbound.dtos.ProductResponseDTO;
import com.application.API_E_commerce.common.response.ApiResponse;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.category.useCase.CategoryUseCase;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/category")
@Tag(name = "Category", description = "Operations pertaining to category management")
public class CategoryController {

	private final CategoryUseCase categoryService;

	public CategoryController (CategoryUseCase categoryService) {
		this.categoryService = categoryService;
	}

	@Operation(
			summary = "Create a category",
			description = "Creates a new category with the provided data"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "201",
					description = "Category created successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid category request data",
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
	public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory (
			@Parameter(description = "Category data to create", required = true)
			@Valid @RequestBody CreateCategoryRequestDTO categoryData) {
		Category category = categoryService.createCategory(categoryData);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Category created successfully", getCategoryResponse(category), HttpStatus.CREATED));
	}

	// Maps a Category entity to a CategoryResponseDTO, including its associated products
	private static CategoryResponseDTO getCategoryResponse (Category category) {
		List<ProductResponseDTO> products = category.getProducts().stream()
				.map(product -> new ProductResponseDTO(
						product.getId(),
						product.getName(),
						product.getDescription(),
						product.getPrice(),
						product.getStock(),
						product.getCreatedAt())
				)
				.toList();

		return new CategoryResponseDTO(
				category.getId(),
				category.getName(),
				category.getDescription(),
				products
		);
	}

	@Operation(
			summary = "Retrieve all categories",
			description = "Returns a list of all registered categories"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Categories retrieved successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class),
							array = @ArraySchema(schema = @Schema(implementation = CategoryResponseDTO.class))
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
	public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getAllCategories () {
		List<CategoryResponseDTO> categories = categoryService.findAllCategories()
				.stream()
				.map(CategoryController::getCategoryResponse)
				.toList();
		return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories, HttpStatus.OK));
	}

	@Operation(
			summary = "Delete a category",
			description = "Deletes the specified category"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "204",
					description = "Category deleted successfully (no content)"
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "Category not found",
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
	@DeleteMapping("/{categoryId}")
	public ResponseEntity<Void> deleteCategory (
			@Parameter(description = "Category ID to delete", required = true)
			@PathVariable UUID categoryId) {
		categoryService.deleteCategoryById(categoryId);
		return ResponseEntity.noContent().build();
	}

}