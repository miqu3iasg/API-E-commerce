package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.OrderUseCases;
import com.application.API_E_commerce.application.usecases.PaymentUseCases;
import com.application.API_E_commerce.application.usecases.ProductUseCases;
import com.application.API_E_commerce.application.usecases.UserUseCases;
import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.order.OrderRepository;
import com.application.API_E_commerce.domain.order.OrderStatus;
import com.application.API_E_commerce.domain.order.dtos.CreateOrderCheckoutDTO;
import com.application.API_E_commerce.domain.order.orderitem.OrderItem;
import com.application.API_E_commerce.domain.order.orderitem.OrderItemUseCases;
import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.payment.PaymentMethod;
import com.application.API_E_commerce.domain.payment.PaymentStatus;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import com.application.API_E_commerce.domain.user.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImplementation implements OrderUseCases {
  private final OrderRepository orderRepository;
  private final UserUseCases userService;
  private final ProductUseCases productService;
  private final PaymentUseCases paymentService;

  public OrderServiceImplementation(
          OrderRepository orderRepository,
          UserUseCases userService, ProductUseCases productService,
          PaymentUseCases paymentService
  ) {
    this.orderRepository = orderRepository;
    this.userService = userService;
    this.productService = productService;
    this.paymentService = paymentService;
  }

  @Override
  @Transactional(rollbackOn = Exception.class)
  public Order createOrderCheckout(CreateOrderCheckoutDTO createOrderCheckoutRequest) {
    validateCreateOrderCheckoutRequest(createOrderCheckoutRequest);

    BigDecimal totalValue = createOrderCheckoutRequest.items().stream()
            .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    Order order = new Order();
    order.setUser(createOrderCheckoutRequest.user());
    order.setStatus(OrderStatus.PENDING);
    order.setTotalValue(totalValue);
    order.setOrderDate(LocalDateTime.now());

    List<OrderItem> orderItems = createOrderCheckoutRequest.items().stream().map(item -> {
      validateProduct(item.getProduct());
      item.setOrder(order);
      item.setUnitPrice(item.getProduct().getPrice());
      return item;
    }).collect(Collectors.toList());

    order.setItems(orderItems);

    Payment payment = processPayment(createOrderCheckoutRequest.paymentMethod(), order);

    orderItems.forEach(item -> {
      Product product = item.getProduct();
      productService.updateStockAfterSale(product.getId(), item.getQuantity());
      product.setStock(item.getProduct().getStock() - item.getQuantity());
    });

    order.setPayment(payment);
    order.setStatus(OrderStatus.PAYMENT);

    return orderRepository.saveOrder(order);
  }

  private void validateCreateOrderCheckoutRequest(CreateOrderCheckoutDTO createOrderCheckoutRequest) {
    if (createOrderCheckoutRequest.user() == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    if (createOrderCheckoutRequest.items() == null || createOrderCheckoutRequest.items().isEmpty()) {
      throw new IllegalArgumentException("Order must have at least one item");
    }
    if (createOrderCheckoutRequest.paymentMethod() == null) {
      throw new IllegalArgumentException("Payment method must be specified");
    }
  }

  private void validateProduct(Product product) {
    if (product == null || productService.findProductById(product.getId()).isEmpty()) {
      throw new IllegalArgumentException("Invalid product");
    }
  }

  private Payment processPayment(PaymentMethod paymentMethod, Order order) {
    Payment payment = new Payment();
    payment.setPaymentMethod(paymentMethod);
    payment.setOrder(order);
    payment.setStatus(PaymentStatus.PENDING);
    payment.setPaymentDate(LocalDateTime.now());
    return paymentService.processPayment(payment);
  }

  @Override
  public List<Order> fetchAllOrderHistory() {
    return orderRepository.findAllOrders();
  }

  @Override
  public List<Order> fetchOrderHistoryByUser(UUID userId) {
    return userService.findUserById(userId)
            .map(User::getOrders)
            .orElseThrow(() -> new IllegalArgumentException("User not found."));
  }

  @Override
  public OrderStatus getOrderStatus(UUID orderId) {
    Optional<Order> existingOrder = this.orderRepository.findOrderById(orderId);

    if (existingOrder.isEmpty()) throw new IllegalArgumentException("Order cannot be null.");

    return existingOrder.get().getStatus();
  }

  @Override
  public Optional<Order> findOrderById(UUID orderId) {
    return this.orderRepository.findOrderById(orderId);
  }

  @Override
  public void cancelOrder(UUID orderId) {
    this.orderRepository.findOrderById(orderId).map(existingOrder -> {
      orderRepository.deleteOrder(orderId);
      return existingOrder;
    }).orElseThrow(() -> new IllegalArgumentException("Order cannot be null."));
  }
}
