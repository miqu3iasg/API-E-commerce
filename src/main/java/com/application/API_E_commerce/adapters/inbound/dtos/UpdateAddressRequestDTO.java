package com.application.API_E_commerce.adapters.inbound.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateAddressRequestDTO(
		@Schema(description = "Address's street for update")
		String street,
		@Schema(description = "Address's city for update")
		String city,
		@Schema(description = "Address's state for update")
		String state,
		@Schema(description = "Address's zipCode for update")
		String zipCode,
		@Schema(description = "Address's country for update")
		String country
) {
}
