package com.application.API_E_commerce.factory;

import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.UserRole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserFactory {
  public static User build() {
    UUID userId = UUID.randomUUID();
    String username = "Jhon";
    String email = "jhon@gmail.com";
    String password = "12345";
    UserRole role = UserRole.CUSTOMER_ROLE;
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime lastLoginAt = LocalDateTime.now().plusHours(1);
    List<Order> orders = new ArrayList<>();
    List<Cart> carts = new ArrayList<>();
    Address address = new Address(UUID.randomUUID(), "street", "city", "state", "zipCode", "country");

    return new User (
            userId,
            username,
            email,
            password,
            role,
            createdAt,
            lastLoginAt,
            orders,
            carts,
            address
    );
  }
}
