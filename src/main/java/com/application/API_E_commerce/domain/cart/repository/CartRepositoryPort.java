package com.application.API_E_commerce.domain.cart.repository;

import com.application.API_E_commerce.domain.cart.Cart;

import java.util.Optional;
import java.util.UUID;

public interface CartRepositoryPort {

	Cart saveCart (Cart cart);

	Optional<Cart> findCartById (UUID cartId);

	void deleteCartById (UUID cartId);

}
