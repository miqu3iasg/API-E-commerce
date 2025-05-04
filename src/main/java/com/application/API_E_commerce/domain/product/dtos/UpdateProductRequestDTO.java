package com.application.API_E_commerce.domain.product.dtos;

import com.application.API_E_commerce.domain.category.Category;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UpdateProductRequestDTO(
		@Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
		@Schema(description = "The product name")
		Optional<String> name,

		@Schema(description = "The product description")
		Optional<String> description,

		@Schema(description = "The product price")
		Optional<BigDecimal> price,

		@Schema(description = "The product stock")
		Optional<Integer> stock,

		@Schema(description = "The product category")
		Optional<Category> category,

		@Schema(description = "The product images url")
		Optional<List<String>> imagesUrl
) { }
