package com.application.API_E_commerce.domain.user.dtos;

import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        UserRole role,
        LocalDateTime createdAt,
        LocalDateTime lastLoginAt
) {

  public UserResponseDTO ( User user ) {
    this(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt(), user.getLastLoginAt());
  }

}