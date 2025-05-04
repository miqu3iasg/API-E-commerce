package com.application.API_E_commerce.domain.category.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateCategoryRequestDTO(
		@NotNull(message = "The name is mandatory")
		@Schema(description = "The category name")
		String name,

		@Schema(description = "The category description")
		String description
) {
}
