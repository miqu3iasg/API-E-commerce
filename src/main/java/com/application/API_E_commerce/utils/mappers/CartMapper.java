package com.application.API_E_commerce.utils.mappers;

import com.application.API_E_commerce.adapters.outbound.entities.cart.JpaCartEntity;
import com.application.API_E_commerce.adapters.outbound.entities.cart.JpaCartItemEntity;
import com.application.API_E_commerce.adapters.outbound.entities.user.JpaUserEntity;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.user.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { UserMapper.class, CartItemMapper.class })
public interface CartMapper {

  CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

  @Mappings({
          @Mapping(source = "id", target = "id"),
          @Mapping(source = "user", target = "user", qualifiedByName = "userToJpa"),
          @Mapping(source = "createdAt", target = "createdAt"),
          @Mapping(source = "cartStatus", target = "cartStatus"),
          @Mapping(source = "totalValue", target = "totalValue"),
          @Mapping(source = "items", target = "items", qualifiedByName = "cartItemsToJpa"),
  })
  JpaCartEntity toJpa(Cart domain);

  @Mappings({
          @Mapping(source = "id", target = "id"),
          @Mapping(source = "user", target = "user", qualifiedByName = "userToDomain"),
          @Mapping(source = "createdAt", target = "createdAt"),
          @Mapping(source = "cartStatus", target = "cartStatus"),
          @Mapping(source = "totalValue", target = "totalValue"),
          @Mapping(source = "items", target = "items", qualifiedByName = "cartItemsToDomain")
  })
  Cart toDomain(JpaCartEntity jpa);

  @Named("userToJpa")
  default JpaUserEntity userToJpa(User user) {
    return user == null ? null : UserMapper.INSTANCE.toJpa(user);
  }

  @Named("userToDomain")
  default User userToDomain(JpaUserEntity jpaUser) {
    return jpaUser == null ? null : UserMapper.INSTANCE.toDomain(jpaUser);
  }

  @Named("cartItemsToJpa")
  default List<JpaCartItemEntity> cartItemsToJpa(List<CartItem> items) {
    return items == null
            ? null
            : items.stream().map(CartItemMapper.INSTANCE::toJpa).collect(Collectors.toList());
  }

  @Named("cartItemsToDomain")
  default List<CartItem> cartItemsToDomain(List<JpaCartItemEntity> items) {
    return items == null
            ? null
            : items.stream().map(CartItemMapper.INSTANCE::toDomain).collect(Collectors.toList());
  }
}
