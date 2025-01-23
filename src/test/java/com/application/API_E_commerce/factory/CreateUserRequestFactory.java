package com.application.API_E_commerce.factory;

import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.user.UserRole;
import com.application.API_E_commerce.domain.user.dtos.CreateUserRequestDTO;

import java.util.UUID;

public class CreateUserRequestFactory {
  public static CreateUserRequestDTO build() {
    Address address = new Address(UUID.randomUUID(), "Street 1", "City", "State", "12345", "Country");

    return new CreateUserRequestDTO(
            "John Doe",
            "john.doe@example.com",
            "password123",
            UserRole.CUSTOMER_ROLE,
            address
    );
  }
}