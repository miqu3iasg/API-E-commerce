package com.application.API_E_commerce.domain.cart.useCase;

import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.payment.PaymentMethod;
import com.stripe.exception.StripeException;

import java.util.List;
import java.util.UUID;

public interface CartUseCase {

	Cart createCart (UUID userId);

	Cart addProductToCart (UUID userId, UUID productId, int quantity);

	Cart removeProductFromCart (UUID userId, UUID productId, UUID cartId);

	void clearCart (UUID userId, UUID cartId);

	void checkoutCart (UUID userId, UUID cartId, PaymentMethod paymentMethod) throws StripeException;

	List<CartItem> getItemsInCart (UUID userId, UUID cartId);

}
