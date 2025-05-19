package com.application.API_E_commerce.adapters.inbound.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserNameRequestDTO(
		@NotNull(message = "The name is required!")
		@Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
		@Schema(description = "User's full name for update")
		String name
) {
}
