package com.application.API_E_commerce.adapters.inbound.dtos;

import com.application.API_E_commerce.common.utils.email.EmailValidation;
import com.application.API_E_commerce.domain.user.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDTO(
		@NotNull(message = "The name is required!")
		@Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
		@Schema(description = "User's full name", example = "Jhon Doe")
		String name,

		@NotNull(message = "The email is required!")
		@EmailValidation
		@Schema(description = "User's email", example = "jhondoe@example.com")
		String email,

		@NotNull(message = "The password is required!")
		@Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
		@Schema(description = "User's password", example = "password123")
		String password,

		@NotNull(message = "The role is required!")
		@Schema(description = "User's role", example = "CUSTOMER_ROLE")
		UserRole role,

		@NotNull(message = "The address is required!")
		@Schema(description = "User's address")
		CreateAddressRequestDTO address
) {
}
