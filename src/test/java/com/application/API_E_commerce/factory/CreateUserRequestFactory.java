package com.application.API_E_commerce.factory;

import com.application.API_E_commerce.adapters.inbound.dtos.CreateAddressRequestDTO;
import com.application.API_E_commerce.adapters.inbound.dtos.CreateUserRequestDTO;
import com.application.API_E_commerce.domain.user.UserRole;

public class CreateUserRequestFactory {

	public static CreateUserRequestDTO build () {
		CreateAddressRequestDTO addressRequestDTO = new CreateAddressRequestDTO("Street 1", "City", "State", "Country", "12345");

		return new CreateUserRequestDTO(
				"John Doe",
				"john.doe@example.com",
				"password123",
				UserRole.CUSTOMER_ROLE,
				addressRequestDTO
		);
	}

}