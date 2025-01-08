package com.application.API_E_commerce.utils.converters;

import com.application.API_E_commerce.adapters.outbound.entities.address.JpaAddressEntity;
import com.application.API_E_commerce.adapters.outbound.entities.user.JpaUserEntity;
import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserConverterTest {
  @InjectMocks
  private UserConverter userConverter;
  @Mock
  private OrderConverter orderConverter;
  @Mock
  private CartConverter cartConverter;
  @Mock
  private AddressConverter addressConverter;

  @Test
  @DisplayName("Should convert User to JpaUser correctly")
  void shouldConvertUserToJpaUser () {
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

    User user = new User (
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

    JpaAddressEntity jpaAddressEntity = new JpaAddressEntity();
    jpaAddressEntity.setId(address.getId());
    jpaAddressEntity.setStreet(address.getStreet());
    jpaAddressEntity.setCity(address.getCity());
    jpaAddressEntity.setState(address.getState());
    jpaAddressEntity.setZipCode(address.getZipCode());
    jpaAddressEntity.setCountry(address.getCountry());

    Mockito.when(addressConverter.toJpa(address)).thenReturn(jpaAddressEntity);

    JpaUserEntity convertedEntity = userConverter.toJpa(user);

    assertNotNull(convertedEntity, "JpaUser should not be null");
    assertEquals(userId, convertedEntity.getId(), "User ID should be the same");
    assertEquals(username, convertedEntity.getName(), "Username should be the same");
    assertEquals(email, convertedEntity.getEmail(), "Email should be the same");
    assertEquals(password, convertedEntity.getPassword(), "Password should be the same");
    assertEquals(role.name(), convertedEntity.getRole().name(), "Role should be converted to String");
    assertEquals(createdAt, convertedEntity.getCreatedAt(), "CreatedAt should be the same");
    assertEquals(lastLoginAt, convertedEntity.getLastLoginAt(), "LastLoginAt should be the same");
    assertEquals(address.getId(), convertedEntity.getAddress().getId(), "Address ID should be the same");
  }
}