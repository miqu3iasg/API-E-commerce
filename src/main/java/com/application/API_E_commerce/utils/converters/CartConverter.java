package com.application.API_E_commerce.utils.converters;

import com.application.API_E_commerce.adapters.outbound.entities.cart.JpaCartEntity;
import com.application.API_E_commerce.adapters.outbound.entities.cart.JpaCartItemEntity;
import com.application.API_E_commerce.adapters.outbound.entities.user.JpaUserEntity;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.utils.mappers.CartItemMapper;
import com.application.API_E_commerce.utils.mappers.UserMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JpaCartConverter {
  private final UserMapper userMapper;
  private final CartItemMapper cartItemMapper;

  public JpaCartConverter (
          UserMapper userMapper,
          CartItemMapper cartItemMapper
  ) {
    this.userMapper = userMapper;
    this.cartItemMapper = cartItemMapper;
  }

  public JpaCartEntity toJpa(Cart domain) {
    if (domain == null) return null; // Exceções personalizadas

    JpaUserEntity jpaUserEntity = domain.getUser() != null
            ? userMapper.toJpa(domain.getUser())
            : null;

    List<JpaCartItemEntity> jpaCartItemEntityList = domain.getItems() != null
            ? domain.getItems().stream().map(cartItemMapper::toJpa).toList()
            : null;

    return JpaCartEntity.fromDomain(domain, jpaUserEntity, jpaCartItemEntityList);
  }

  public Cart toDomain(JpaCartEntity jpa) {
    if (jpa == null) return null;

    User domainUser = jpa.getUser() != null
            ? userMapper.toDomain(jpa.getUser())
            : null;

    List<CartItem> cartItemList = jpa.getItems() != null
            ? jpa.getItems().stream().map(cartItemMapper::toDomain).toList()
            : null;

    Cart cart = new Cart();
    cart.setId(jpa.getId());
    cart.setUser(domainUser);
    cart.setItems(cartItemList);
    cart.setCreatedAt(jpa.getCreatedAt());

    return cart;
  }
}
