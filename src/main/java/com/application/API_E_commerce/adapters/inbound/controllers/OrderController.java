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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderUseCases orderService;

  public OrderController ( OrderUseCases orderService ) {
    this.orderService = orderService;
  }

  @PostMapping("/checkout")
  public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrderCheckout (
          @RequestBody CreateOrderCheckoutDTO createOrderCheckoutRequest
  ) throws StripeException {
    Order order = orderService.createOrderCheckout(createOrderCheckoutRequest);

    OrderResponseDTO response = getOrderResponse(order);

    return ResponseEntity.ok(ApiResponse.success("Order checkout was created successfully", response, HttpStatus.OK));
  }

  @GetMapping("/history")
  public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> fetchAllOrderHistory () {
    List<Order> orders = orderService.fetchAllOrderHistory();

    List<OrderResponseDTO> response = orders.stream()
            .map(OrderController::getOrderResponse)
            .toList();

    return ResponseEntity.ok(ApiResponse.success("Order history fetched successfully", response, HttpStatus.OK)
    );
  }

  @GetMapping("/history/user/{userId}")
  public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> fetchOrderHistoryByUser ( @PathVariable UUID userId ) {

    List<Order> orders = orderService.fetchOrderHistoryByUser(userId);

    List<OrderResponseDTO> response = orders.stream()
            .map(OrderController::getOrderResponse)
            .toList();

    return ResponseEntity.ok(ApiResponse.success("Order history for user fetched successfully", response, HttpStatus.OK)
    );
  }

  @GetMapping("/{orderId}/status")
  public ResponseEntity<ApiResponse<OrderStatusResponseDTO>> getOrderStatus ( @PathVariable UUID orderId ) {

    OrderStatus status = orderService.getOrderStatus(orderId);

    return ResponseEntity.ok(ApiResponse.success("Order status fetched successfully", new OrderStatusResponseDTO(status), HttpStatus.OK)
    );
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<ApiResponse<OrderResponseDTO>> findOrderById ( @PathVariable UUID orderId ) {

    return orderService.findOrderById(orderId)
            .map(order -> ResponseEntity.ok(
                    ApiResponse.success("Order found successfully", getOrderResponse(order), HttpStatus.OK)
            ))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Order not found", HttpStatus.NOT_FOUND))
            );
  }

  @DeleteMapping("/{orderId}")
  public ResponseEntity<ApiResponse<Void>> cancelOrder ( @PathVariable UUID orderId ) {

    orderService.cancelOrder(orderId);

    return ResponseEntity.ok(ApiResponse.success("Order canceled successfully", null, HttpStatus.OK)
    );
  }

  private static OrderResponseDTO getOrderResponse ( Order order ) {

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

}
