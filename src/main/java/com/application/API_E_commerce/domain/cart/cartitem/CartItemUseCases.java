package com.application.API_E_commerce.domain.cart.cartitem;

import java.util.Optional;
import java.util.UUID;

public interface CartItemUseCases {
  Optional<CartItem> findCartItemById(UUID cartItemId);
}
