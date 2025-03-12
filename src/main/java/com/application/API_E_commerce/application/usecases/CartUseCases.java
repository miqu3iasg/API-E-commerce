package com.application.API_E_commerce.application.usecases;

import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.payment.PaymentMethod;

import java.util.List;
import java.util.UUID;

public interface CartUseCases {
  Cart addProductToCart(UUID userId, UUID productId, int quantity);
  void removeProductFromCart(UUID userId, UUID productId, UUID cartId);
  void updateProductQuantityInCart(UUID userId, UUID productId, int quantity);
  void clearCart(UUID userId);
  void checkoutCart(UUID userId, UUID cartId, PaymentMethod paymentMethod);
  List<CartItem> getItemsInCart(UUID userId, UUID cartId);
}
