package com.application.API_E_commerce.utils.mappers;

import com.application.API_E_commerce.adapters.outbound.entities.cart.JpaCartEntity;
import com.application.API_E_commerce.adapters.outbound.entities.order.JpaOrderEntity;
import com.application.API_E_commerce.adapters.outbound.entities.user.JpaUserEntity;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.user.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { OrderMapper.class, CartMapper.class, AddressMapper.class })
public interface UserMapper {

  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  @Mappings({
          @Mapping(source = "id", target = "id"),
          @Mapping(source = "name", target = "name"),
          @Mapping(source = "email", target = "email"),
          @Mapping(source = "password", target = "password"),
          @Mapping(source = "role", target = "role"),
          @Mapping(source = "createdAt", target = "createdAt"),
          @Mapping(source = "lastLoginAt", target = "lastLoginAt"),
          @Mapping(source = "orders", target = "orders", qualifiedByName = "ordersToJpa"),
          @Mapping(source = "carts", target = "carts", qualifiedByName = "cartsToJpa"),
          @Mapping(source = "address", target = "address", qualifiedByName = "addressToJpa")
  })
  JpaUserEntity toJpa(User domain);

  @Mappings({
          @Mapping(source = "id", target = "id"),
          @Mapping(source = "name", target = "name"),
          @Mapping(source = "email", target = "email"),
          @Mapping(source = "password", target = "password"),
          @Mapping(source = "role", target = "role"),
          @Mapping(source = "createdAt", target = "createdAt"),
          @Mapping(source = "lastLoginAt", target = "lastLoginAt"),
          @Mapping(source = "orders", target = "orders", qualifiedByName = "ordersToDomain"),
          @Mapping(source = "carts", target = "carts", qualifiedByName = "cartsToDomain"),
          @Mapping(source = "address", target = "address", qualifiedByName = "addressToDomain")
  })
  User toDomain(JpaUserEntity jpa);

  @Named("ordersToJpa")
  default List<JpaOrderEntity> ordersToJpa(List<Order> orders) {
    return orders != null ? orders.stream().map(OrderMapper.INSTANCE::toJpa).collect(Collectors.toList()) : null;
  }

  @Named("ordersToDomain")
  default List<Order> ordersToDomain(List<JpaOrderEntity> jpaOrders) {
    return jpaOrders != null ? jpaOrders.stream().map(OrderMapper.INSTANCE::toDomain).collect(Collectors.toList()) : null;
  }

  @Named("cartsToJpa")
  default List<JpaCartEntity> cartsToJpa(List<Cart> carts) {
    return carts != null
            ? carts.stream().map(CartMapper.INSTANCE::toJpa).collect(Collectors.toList())
            : null;
  }

  @Named("cartsToDomain")
  default List<Cart> cartsToDomain(List<JpaCartEntity> jpaCarts) {
    return jpaCarts != null
            ? jpaCarts.stream().map(CartMapper.INSTANCE::toDomain).collect(Collectors.toList())
            : null;
  }
}
