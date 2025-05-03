package com.application.API_E_commerce.adapters.inbound.controllers;

import com.application.API_E_commerce.application.usecases.OrderUseCases;
import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.order.OrderStatus;
import com.application.API_E_commerce.domain.order.dtos.CreateOrderCheckoutDTO;
import com.application.API_E_commerce.domain.order.dtos.OrderResponseDTO;
import com.application.API_E_commerce.domain.order.dtos.OrderStatusResponseDTO;
import com.application.API_E_commerce.domain.payment.dtos.PaymentResponseDTO;
import com.application.API_E_commerce.domain.user.dtos.UserResponseDTO;
import com.application.API_E_commerce.response.ApiResponse;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order", description = "Operations pertaining to order management")
public class OrderController {

	private final OrderUseCases orderService;

	public OrderController (OrderUseCases orderService) {
		this.orderService = orderService;
	}

	@Operation(
			summary = "Create an order with checkout",
			description = "Creates a new order with checkout using the provided data"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "201",
					description = "Order created successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid order data or payment validation error",
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
	@PostMapping("/checkout")
	public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrderCheckout (
			@Parameter(description = "Order data to create with checkout", required = true)
			@RequestBody CreateOrderCheckoutDTO orderData) throws StripeException {
		Order order = orderService.createOrderCheckout(orderData);
		OrderResponseDTO response = getOrderResponse(order);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Order created successfully", response, HttpStatus.CREATED));
	}

	private static OrderResponseDTO getOrderResponse (Order order) {
		PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO(
				order.getPayment().getId(),
				order.getPayment().getAmountInCents(),
				order.getPayment().getDescription(),
				order.getPayment().getPaymentMethod(),
				order.getPayment().getCurrency(),
				order.getPayment().getStatus(),
				order.getPayment().getPaymentDate()
		);

		UserResponseDTO userResponseDTO = new UserResponseDTO(
				order.getUser().getId(),
				order.getUser().getName(),
				order.getUser().getEmail(),
				order.getUser().getRole(),
				order.getUser().getCreatedAt(),
				order.getUser().getLastLoginAt()
		);

		return new OrderResponseDTO(
				order.getId(),
				order.getStatus(),
				order.getOrderDate(),
				order.getTotalValue(),
				order.getCurrency(),
				order.getDescription(),
				order.getItems(),
				paymentResponseDTO,
				userResponseDTO
		);
	}

	@Operation(
			summary = "Retrieve all order history",
			description = "Returns a list of all registered orders",
			tags = {"Order history"}
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Order history retrieved successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class),
							array = @ArraySchema(schema = @Schema(implementation = OrderResponseDTO.class))
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
	@GetMapping("/history")
	public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> fetchAllOrderHistory () {
		List<Order> orders = orderService.fetchAllOrderHistory();
		List<OrderResponseDTO> response = orders.stream()
				.map(OrderController::getOrderResponse)
				.toList();
		return ResponseEntity.ok(ApiResponse.success("Order history retrieved successfully", response, HttpStatus.OK));
	}

	@Operation(
			summary = "Retrieve order history by user",
			description = "Returns a list of orders for the specified user",
			tags = {"Order history"}
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "User order history retrieved successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class),
							array = @ArraySchema(schema = @Schema(implementation = OrderResponseDTO.class))
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
	@GetMapping("/history/user/{userId}")
	public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> fetchOrderHistoryByUser (
			@Parameter(description = "User ID to retrieve order history", required = true)
			@PathVariable UUID userId) {
		List<Order> orders = orderService.fetchOrderHistoryByUser(userId);
		List<OrderResponseDTO> response = orders.stream()
				.map(OrderController::getOrderResponse)
				.toList();
		return ResponseEntity.ok(ApiResponse.success("User order history retrieved successfully", response, HttpStatus.OK));
	}

	@Operation(
			summary = "Retrieve order status",
			description = "Returns the status of the specified order",
			tags = {"Order status"}
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Order status retrieved successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "Order not found",
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
	@GetMapping("/{orderId}/status")
	public ResponseEntity<ApiResponse<OrderStatusResponseDTO>> getOrderStatus (
			@Parameter(description = "Order ID to retrieve status", required = true)
			@PathVariable UUID orderId) {
		OrderStatus status = orderService.getOrderStatus(orderId);
		return ResponseEntity.ok(ApiResponse.success("Order status retrieved successfully", new OrderStatusResponseDTO(status), HttpStatus.OK));
	}

	@Operation(
			summary = "Retrieve an order by ID",
			description = "Returns the details of the specified order"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Order retrieved successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "Order not found for the provided ID",
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
	@GetMapping("/{orderId}")
	public ResponseEntity<ApiResponse<OrderResponseDTO>> findOrderById (
			@Parameter(description = "Order ID to retrieve", required = true)
			@PathVariable UUID orderId) {
		return orderService.findOrderById(orderId)
				.map(order -> ResponseEntity.ok(
						ApiResponse.success("Order retrieved successfully", getOrderResponse(order), HttpStatus.OK)
				))
				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(ApiResponse.error("Order not found for the provided ID", HttpStatus.NOT_FOUND)));
	}

	@Operation(
			summary = "Cancel an order",
			description = "Cancels the specified order"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "204",
					description = "Order canceled successfully (no content)"
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "Order not found",
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
	@DeleteMapping("/{orderId}")
	public ResponseEntity<Void> cancelOrder (
			@Parameter(description = "Order ID to cancel", required = true)
			@PathVariable UUID orderId) {
		orderService.cancelOrder(orderId);
		return ResponseEntity.noContent().build();
	}

}