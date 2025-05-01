package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.cart.cartitem.CartItemRepository;
import com.application.API_E_commerce.domain.cart.cartitem.CartItemUseCases;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.infrastructure.exceptions.cart.UserCartNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.product.InvalidQuantityException;
import com.application.API_E_commerce.infrastructure.exceptions.product.MissingProductIdException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CartItemServiceImplementation implements CartItemUseCases {

	private final CartItemRepository cartItemRepository;

	public CartItemServiceImplementation (CartItemRepository cartItemRepository) {
		this.cartItemRepository = cartItemRepository;
	}

	@Override
	public CartItem createCartItem (Product product, Cart cart, int quantity) {
		validateCartItemRequest(product, cart, quantity);

		CartItem newCartItem = new CartItem();

		newCartItem.setProduct(product);
		newCartItem.setCart(cart);
		newCartItem.setQuantity(quantity);

		return cartItemRepository.saveCartItem(newCartItem);
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
		return cartItemRepository.findCartItemById(cartItemId);
	}

}
