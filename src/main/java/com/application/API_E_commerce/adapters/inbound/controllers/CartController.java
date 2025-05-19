package com.application.API_E_commerce.adapters.inbound.controllers;

import com.application.API_E_commerce.adapters.inbound.dtos.AddProductToCartRequestDTO;
import com.application.API_E_commerce.adapters.inbound.dtos.CartResponseDTO;
import com.application.API_E_commerce.adapters.inbound.dtos.UserResponseDTO;
import com.application.API_E_commerce.common.response.ApiResponse;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.cart.useCase.CartUseCase;
import com.application.API_E_commerce.domain.payment.PaymentMethod;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "Cart", description = "Operations pertaining to cart management")
public class CartController {

	private final CartUseCase cartService;

	public CartController (CartUseCase cartService) {
		this.cartService = cartService;
	}

	@Operation(
			summary = "Create a cart",
			description = "Creates a new cart for the specified user"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "201",
					description = "Cart created successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "User not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@PostMapping("/user/{userId}/cart")
	public ResponseEntity<ApiResponse<CartResponseDTO>> createCart (
			@Parameter(description = "User ID to create the cart", required = true)
			@PathVariable UUID userId) {
		Cart cart = cartService.createCart(userId);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Cart created successfully", toResponse(cart), HttpStatus.CREATED));
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

	@Operation(
			summary = "Add product to cart",
			description = "Adds a product to the user's cart with the specified quantity",
			tags = {"Cart items"}
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Product added to cart successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid quantity or request data",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "User, product, or cart not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@PostMapping("/user/{userId}/product/{productId}")
	public ResponseEntity<ApiResponse<CartResponseDTO>> addProductToCart (
			@Parameter(description = "User ID associated with the cart", required = true)
			@PathVariable UUID userId,
			@Parameter(description = "Product ID to add to the cart", required = true)
			@PathVariable UUID productId,
			@Parameter(description = "Product quantity data to add", required = true)
			@Valid @RequestBody AddProductToCartRequestDTO productData) {
		Cart cart = cartService.addProductToCart(userId, productId, productData.quantity());
		return ResponseEntity.ok(ApiResponse.success("Product added to cart successfully", toResponse(cart), HttpStatus.OK));
	}

	@Operation(
			summary = "Remove product from cart",
			description = "Removes a product from the specified cart",
			tags = {"Cart items"}
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Product removed from cart successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "User, product, or cart not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@DeleteMapping("/user/{userId}/cart/{cartId}/product/{productId}")
	public ResponseEntity<ApiResponse<CartResponseDTO>> removeProductFromCart (
			@Parameter(description = "User ID associated with the cart", required = true)
			@PathVariable UUID userId,
			@Parameter(description = "Cart ID to remove the product from", required = true)
			@PathVariable UUID cartId,
			@Parameter(description = "Product ID to remove from the cart", required = true)
			@PathVariable UUID productId) {
		Cart cart = cartService.removeProductFromCart(userId, productId, cartId);
		return ResponseEntity.ok(ApiResponse.success("Product removed from cart successfully", toResponse(cart), HttpStatus.OK));
	}

	@Operation(
			summary = "Clear cart",
			description = "Clears all items from the specified cart"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "204",
					description = "Cart cleared successfully (no content)"
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "User or cart not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@DeleteMapping("/user/{userId}/cart/{cartId}/clear")
	public ResponseEntity<Void> clearCart (
			@Parameter(description = "User ID associated with the cart", required = true)
			@PathVariable UUID userId,
			@Parameter(description = "Cart ID to clear", required = true)
			@PathVariable UUID cartId) {
		cartService.clearCart(userId, cartId);
		return ResponseEntity.noContent().build();
	}

	@Operation(
			summary = "Checkout cart",
			description = "Processes the checkout for the specified cart using the provided payment method",
			tags = {"Cart checkout"}
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "201",
					description = "Cart checkout completed successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid payment method or validation error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "User or cart not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error or Stripe API failure",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@PostMapping("/user/{userId}/cart/{cartId}/checkout")
	public ResponseEntity<ApiResponse<Void>> checkoutCart (
			@Parameter(description = "User ID associated with the cart", required = true)
			@PathVariable UUID userId,
			@Parameter(description = "Cart ID to checkout", required = true)
			@PathVariable UUID cartId,
			@Parameter(
					description = "Payment method for checkout. Can be 'CARD' or 'BOLETO'",
					required = true
			)
			@RequestBody PaymentMethod paymentMethod) throws StripeException {
		cartService.checkoutCart(userId, cartId, paymentMethod);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Cart checkout completed successfully", null, HttpStatus.CREATED));
	}

	@Operation(
			summary = "Retrieve cart items",
			description = "Returns the list of items in the specified cart",
			tags = {"Cart items"}
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Cart items retrieved successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class),
							array = @ArraySchema(schema = @Schema(implementation = CartItem.class))
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "User or cart not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@GetMapping("/user/{userId}/cart/{cartId}")
	public ResponseEntity<ApiResponse<List<CartItem>>> getItemsInCart (
			@Parameter(description = "User ID associated with the cart", required = true)
			@PathVariable UUID userId,
			@Parameter(description = "Cart ID to retrieve items", required = true)
			@PathVariable UUID cartId) {
		List<CartItem> items = cartService.getItemsInCart(userId, cartId);
		return ResponseEntity.ok(ApiResponse.success("Cart items retrieved successfully", items, HttpStatus.OK));
	}

}