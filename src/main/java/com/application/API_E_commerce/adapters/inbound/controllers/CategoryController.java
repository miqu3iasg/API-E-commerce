package com.application.API_E_commerce.adapters.inbound.controllers;

import com.application.API_E_commerce.application.usecases.CategoryUseCases;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.category.dtos.CategoryResponseDTO;
import com.application.API_E_commerce.domain.category.dtos.CreateCategoryRequestDTO;
import com.application.API_E_commerce.domain.product.dtos.ProductResponseDTO;
import com.application.API_E_commerce.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {

	private final CategoryUseCases categoryService;

	public CategoryController (CategoryUseCases categoryService) {
		this.categoryService = categoryService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory (@RequestBody CreateCategoryRequestDTO createCategoryRequest) {
		Category category = categoryService.createCategory(createCategoryRequest);

		return ResponseEntity.ok(ApiResponse.success("Category created successfully", getCategoryResponse(category), HttpStatus.OK));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getAllCategories () {
		List<CategoryResponseDTO> categories = categoryService.findAllCategories().stream()
				.map(CategoryController::getCategoryResponse)
				.toList();

		return ResponseEntity.ok(ApiResponse.success("Categories fetched successfully", categories, HttpStatus.OK));
	}

	@DeleteMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<Void>> deleteCategory (@PathVariable UUID categoryId) {
		categoryService.deleteCategoryById(categoryId);

		return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null, HttpStatus.OK));
	}

	private static CategoryResponseDTO getCategoryResponse (Category category) {
		List<ProductResponseDTO> products = category.getProducts().stream()
				.map(product -> new ProductResponseDTO(
						product.getId(),
						product.getName(),
						product.getDescription(),
						product.getPrice(),
						product.getStock(),
						product.getCreatedAt())
				).toList();

		return new CategoryResponseDTO(
				category.getId(),
				category.getName(),
				category.getDescription(),
				products
		);
	}

}
