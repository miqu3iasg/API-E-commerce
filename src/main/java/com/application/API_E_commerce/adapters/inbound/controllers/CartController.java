package com.application.API_E_commerce.adapters.inbound.controllers;

import com.application.API_E_commerce.application.usecases.CartUseCases;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.cart.dtos.AddProductToCartRequestDTO;
import com.application.API_E_commerce.domain.cart.dtos.CartResponseDTO;
import com.application.API_E_commerce.domain.payment.PaymentMethod;
import com.application.API_E_commerce.domain.user.dtos.UserResponseDTO;
import com.application.API_E_commerce.response.ApiResponse;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

	private final CartUseCases cartService;

	public CartController (CartUseCases cartService) {
		this.cartService = cartService;
	}

	@PostMapping("/user/{userId}/cart")
	public ResponseEntity<ApiResponse<CartResponseDTO>> createCart (@PathVariable UUID userId) {
		Cart cart = cartService.createCart(userId);

		return ResponseEntity.ok(ApiResponse.success("Cart created successfully", toResponse(cart), HttpStatus.OK));
	}

	@PostMapping("/user/{userId}/product/{productId}")
	public ResponseEntity<ApiResponse<CartResponseDTO>> addProductToCart (
			@PathVariable UUID userId,
			@PathVariable UUID productId,
			@RequestBody AddProductToCartRequestDTO addProductToCartRequest
	) {
		Cart cart = cartService.addProductToCart(userId, productId, addProductToCartRequest.quantity());

		return ResponseEntity.ok(ApiResponse.success("Product added to cart successfully", toResponse(cart), HttpStatus.OK));
	}

	@PutMapping("/{cartId}/user/{userId}/product/{productId}/remove")
	public ResponseEntity<ApiResponse<CartResponseDTO>> removeProductFromCart (
			@PathVariable UUID cartId,
			@PathVariable UUID userId,
			@PathVariable UUID productId
	) {
		cartService.removeProductFromCart(userId, productId, cartId);

		return ResponseEntity.ok(ApiResponse.success("Product removed from cart successfully", null, HttpStatus.OK));
	}

	@DeleteMapping("/user/{userId}/cart/{cartId}/clear")
	public ResponseEntity<ApiResponse<Void>> clearCart (@PathVariable UUID userId, @PathVariable UUID cartId) {
		cartService.clearCart(userId, cartId);

		return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully", null, HttpStatus.OK));
	}

	@PostMapping("/user/{userId}/cart/{cartId}/checkout")
	public ResponseEntity<ApiResponse<Void>> checkoutCart (
			@PathVariable UUID userId,
			@PathVariable UUID cartId,
			@RequestBody PaymentMethod paymentMethod
	) throws StripeException {
		cartService.checkoutCart(userId, cartId, paymentMethod);

		return ResponseEntity.ok(ApiResponse.success("Cart checked out successfully", null, HttpStatus.OK));
	}

	@GetMapping("/user/{userId}/cart/{cartId}")
	public ResponseEntity<ApiResponse<List<CartItem>>> getItemsInCart (@PathVariable UUID userId, @PathVariable UUID cartId) {
		List<CartItem> items = cartService.getItemsInCart(userId, cartId);

		return ResponseEntity.ok(ApiResponse.success("Items in cart fetched successfully", items, HttpStatus.OK));
	}

	private static CartResponseDTO toResponse (Cart cart) {
		UserResponseDTO userResponse = new UserResponseDTO(
				cart.getUser().getId(),
				cart.getUser().getName(),
				cart.getUser().getEmail(),
				cart.getUser().getRole(),
				cart.getUser().getCreatedAt(),
				cart.getUser().getLastLoginAt()
		);
		return new CartResponseDTO(
				cart.getId(),
				userResponse,
				cart.getCreatedAt(),
				cart.getCartStatus(),
				cart.getTotalValue(),
				cart.getItems()
		);
	}

}
