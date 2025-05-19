package com.application.API_E_commerce.domain.cart.cartitem.services;

import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.cart.cartitem.repository.CartItemRepositoryPort;
import com.application.API_E_commerce.domain.cart.cartitem.useCase.CartItemUseCase;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.infrastructure.exceptions.cart.UserCartNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.product.InvalidQuantityException;
import com.application.API_E_commerce.infrastructure.exceptions.product.MissingProductIdException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CartItemService implements CartItemUseCase {

	private final CartItemRepositoryPort cartItemRepositoryPort;

	public CartItemService (CartItemRepositoryPort cartItemRepositoryPort) {
		this.cartItemRepositoryPort = cartItemRepositoryPort;
	}

	@Override
	public CartItem createCartItem (Product product, Cart cart, int quantity) {
		validateCartItemRequest(product, cart, quantity);

		CartItem newCartItem = new CartItem();

		newCartItem.setProduct(product);
		newCartItem.setCart(cart);
		newCartItem.setQuantity(quantity);

		return cartItemRepositoryPort.saveCartItem(newCartItem);
	}

	private static void validateCartItemRequest (Product product, Cart cart, int quantity) {
		if (product == null)
			throw new MissingProductIdException("Product id cannot be null.");
		if (cart == null)
			throw new UserCartNotFoundException("Cart cannot be null.");
		if (quantity <= 0)
			throw new InvalidQuantityException("Quantity must be greater than 0.");
	}

	@Override
	public Optional<CartItem> findCartItemById (UUID cartItemId) {
		return cartItemRepositoryPort.findCartItemById(cartItemId);
	}

}
