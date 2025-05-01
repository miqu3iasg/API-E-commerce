package com.application.API_E_commerce.factory;

import com.application.API_E_commerce.domain.address.dtos.CreateAddressRequestDTO;
import com.application.API_E_commerce.domain.user.UserRole;
import com.application.API_E_commerce.domain.user.dtos.CreateUserRequestDTO;

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