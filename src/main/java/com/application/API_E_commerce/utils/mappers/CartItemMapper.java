package com.application.API_E_commerce.utils.mappers;

import com.application.API_E_commerce.adapters.outbound.entities.cart.JpaCartEntity;
import com.application.API_E_commerce.adapters.outbound.entities.cart.JpaCartItemEntity;
import com.application.API_E_commerce.adapters.outbound.entities.product.JpaProductEntity;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.product.Product;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {  CartMapper.class, ProductMapper.class })
public interface CartItemMapper {

  CartItemMapper INSTANCE = Mappers.getMapper(CartItemMapper.class);

  @Mappings({
          @Mapping(source = "id", target = "id"),
          @Mapping(source = "cart", target = "cart", qualifiedByName = "cartToJpa"),
          @Mapping(source = "product", target = "product", qualifiedByName = "productToJpa"),
          @Mapping(source = "quantity", target = "quantity"),
  })
  JpaCartItemEntity toJpa(CartItem domain);

  @Mappings({
          @Mapping(source = "id", target = "id"),
          @Mapping(source = "cart", target = "cart", qualifiedByName = "cartToDomain"),
          @Mapping(source = "product", target = "product", qualifiedByName = "productToDomain"),
          @Mapping(source = "quantity", target = "quantity"),
  })
  CartItem toDomain(JpaCartItemEntity jpa);

  @Named("cartToJpa")
  default JpaCartEntity cartToJpa(Cart cart) {
    return cart == null ? null : CartMapper.INSTANCE.toJpa(cart);
  }

  @Named("cartToDomain")
  default Cart cartToDomain(JpaCartEntity jpaCart) {
    return jpaCart == null ? null : CartMapper.INSTANCE.toDomain(jpaCart);
  }
}
