package com.application.API_E_commerce.adapters.inbound.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateProductRequestDTO(
		@NotNull(message = "The name is mandatory")
		@Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
		@Schema(description = "The product name")
		String name,

		@Schema(description = "The product description")
		String description,

		@NotNull(message = "The price is mandatory")
		@Schema(description = "The product price")
		@Positive(message = "The price must be positive")
		@Digits(integer = 10, fraction = 2, message = "The price must be a number with 2 decimal places")
		BigDecimal price,

		@NotNull(message = "The stock is mandatory")
		@Schema(description = "The product stock")
		@Positive(message = "The stock must be positive")
		int stock
) {
}
