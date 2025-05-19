package com.application.API_E_commerce.domain.cart.cartitem.repository;

import com.application.API_E_commerce.domain.cart.cartitem.CartItem;

import java.util.Optional;
import java.util.UUID;

public interface CartItemRepositoryPort {

	CartItem saveCartItem (CartItem cartItem);

	void deleteCartItem (CartItem cartItem);

	Optional<CartItem> findCartItemById (UUID id);

	void deleteCartItemById (UUID cartItemId);

}
