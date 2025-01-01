package com.application.API_E_commerce.domain.user;

public enum UserRole {
  ADMIN_ROLE("admin"),
  CUSTOMER_ROLE("customer");

  private final String role;

  UserRole (String role) {
    this.role = role;
  }

  String getRole () {
    return role;
  }
}
