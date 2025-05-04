package com.application.API_E_commerce.domain.cart.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddProductToCartRequestDTO(
		@NotNull(message = "The quantity is mandatory")
		@Positive(message = "The quantity must be positive")
		@Schema(description = "The quantity of products that will be added")
		int quantity
) {
}
