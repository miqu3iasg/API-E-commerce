package com.application.API_E_commerce.utils.converters;

import com.application.API_E_commerce.adapters.outbound.entities.address.JpaAddressEntity;
import com.application.API_E_commerce.adapters.outbound.entities.user.JpaUserEntity;
import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.UserRole;
import com.application.API_E_commerce.factory.JpaAddressEntityFactory;
import com.application.API_E_commerce.factory.UserFactory;
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
    User user = UserFactory.build();

    JpaAddressEntity jpaAddressEntity = JpaAddressEntityFactory.build(user.getAddress());

    Mockito.when(addressConverter.toJpa(user.getAddress())).thenReturn(jpaAddressEntity);

    JpaUserEntity convertedEntity = userConverter.toJpa(user);

    assertNotNull(convertedEntity, "JpaUser should not be null");
    assertEquals(user.getId(), convertedEntity.getId(), "User ID should be the same");
    assertEquals(user.getName(), convertedEntity.getName(), "Username should be the same");
    assertEquals(user.getEmail(), convertedEntity.getEmail(), "Email should be the same");
    assertEquals(user.getPassword(), convertedEntity.getPassword(), "Password should be the same");
    assertEquals(user.getRole().name(), convertedEntity.getRole().name(), "Role should be converted to String");
    assertEquals(user.getCreatedAt(), convertedEntity.getCreatedAt(), "CreatedAt should be the same");
    assertEquals(user.getLastLoginAt(), convertedEntity.getLastLoginAt(), "LastLoginAt should be the same");
    assertEquals(user.getAddress().getId(), convertedEntity.getAddress().getId(), "Address ID should be the same");
  }
}