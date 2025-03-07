package com.application.API_E_commerce.domain.cart;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository {
  Cart saveCart(Cart cart);
  Optional<Cart> findCartById(UUID cartId);
  void deleteCartById(UUID cartId);
}
