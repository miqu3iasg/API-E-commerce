package com.application.API_E_commerce.utils.converters;

import com.application.API_E_commerce.adapters.outbound.entities.address.JpaAddressEntity;
import com.application.API_E_commerce.adapters.outbound.entities.cart.JpaCartEntity;
import com.application.API_E_commerce.adapters.outbound.entities.order.JpaOrderEntity;
import com.application.API_E_commerce.adapters.outbound.entities.user.JpaUserEntity;
import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.user.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserConverter {
  private final OrderConverter orderConverter;
  private final CartConverter cartConverter;
  private final AddressConverter addressConverter;

  public UserConverter (
          OrderConverter orderConverter,
          CartConverter cartConverter,
          AddressConverter addressConverter
  ) {
    this.orderConverter = orderConverter;
    this.cartConverter = cartConverter;
    this.addressConverter = addressConverter;
  }

  public JpaUserEntity toJpa(User domain) {
    if (domain == null) return null;

    List<JpaOrderEntity> jpaOrderEntityList = domain.getOrders() != null
            ? domain.getOrders().stream().map(orderConverter::toJpa).toList()
            : null;

    List<JpaCartEntity> jpaCartEntityList = domain.getCarts() != null
            ? domain.getCarts().stream().map(cartConverter::toJpa).toList()
            : null;

    JpaAddressEntity jpaAddressEntity = domain.getAddress() != null
            ? addressConverter.toJpa(domain.getAddress())
            : null;

    return JpaUserEntity.fromDomain (
            domain,
            jpaOrderEntityList,
            jpaCartEntityList,
            jpaAddressEntity
    );
  }

  public User toDomain(JpaUserEntity jpaUserEntity) {
    if (jpaUserEntity == null) return null;

    List<Order> orderList = jpaUserEntity.getOrders() != null
            ? jpaUserEntity.getOrders().stream().map(orderConverter::toDomain).toList()
            : null;

    List<Cart> cartList = jpaUserEntity.getCarts() != null
            ? jpaUserEntity.getCarts().stream().map(cartConverter::toDomain).toList()
            : null;

    Address address = jpaUserEntity.getAddress() != null
            ? addressConverter.toDomain(jpaUserEntity.getAddress())
            : null;

    User user = new User();
    user.setId(jpaUserEntity.getId());
    user.setName(jpaUserEntity.getName());
    user.setEmail(jpaUserEntity.getEmail());
    user.setPassword(jpaUserEntity.getPassword());
    user.setRole(jpaUserEntity.getRole());
    user.setCreatedAt(jpaUserEntity.getCreatedAt());
    user.setLastLoginAt(jpaUserEntity.getLastLoginAt());
    user.setOrders(orderList);
    user.setCarts(cartList);
    user.setAddress(address);

    return user;
  }
}
