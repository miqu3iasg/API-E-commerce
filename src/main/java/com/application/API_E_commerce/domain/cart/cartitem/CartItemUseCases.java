package com.application.API_E_commerce.domain.cart.cartitem;

import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.cartitem.dtos.CreateCartItemDTO;
import com.application.API_E_commerce.domain.product.Product;

import java.util.Optional;
import java.util.UUID;

public interface CartItemUseCases {
  CartItem createCartItem(Product product, Cart cart, int quantity);
  Optional<CartItem> findCartItemById(UUID cartItemId);
}
