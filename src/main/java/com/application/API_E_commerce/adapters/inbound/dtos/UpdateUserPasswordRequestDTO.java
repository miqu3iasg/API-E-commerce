package com.application.API_E_commerce.adapters.inbound.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateUserPasswordRequestDTO(
		@NotNull(message = "The password is required!")
		@Schema(
				description = "User's password for update",
				example = "newPassword123"
		)
		String password
) {
}
