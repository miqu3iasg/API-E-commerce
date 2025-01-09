package com.application.API_E_commerce.domain.user.dtos;

import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.user.UserRole;

public record CreateUserRequestDTO (
        String name,
        String email,
        String password,
        UserRole role,
        Address address
) {
}
