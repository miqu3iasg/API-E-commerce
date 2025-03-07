package com.application.API_E_commerce.application.usecases;

import java.util.UUID;

public interface CartUseCases {
  void addProductToCart(UUID userId, UUID productId, int quantity);
  void removeProductFromCart(UUID userId, UUID productId);
  void updateProductQuantityInCart(UUID userId, UUID productId, int quantity);
  void clearCart(UUID userId);
  void checkoutCart(UUID userId);
}
