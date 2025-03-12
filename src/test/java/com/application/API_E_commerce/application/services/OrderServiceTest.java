package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.PaymentUseCases;
import com.application.API_E_commerce.application.usecases.ProductUseCases;
import com.application.API_E_commerce.application.usecases.UserUseCases;
import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.order.OrderRepository;
import com.application.API_E_commerce.domain.order.OrderStatus;
import com.application.API_E_commerce.domain.order.dtos.CreateOrderCheckoutDTO;
import com.application.API_E_commerce.domain.order.orderitem.OrderItem;
import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.payment.PaymentMethod;
import com.application.API_E_commerce.domain.payment.PaymentStatus;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.UserRole;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
  @InjectMocks
  private OrderServiceImplementation orderService;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private ProductUseCases productService;

  @Mock
  private UserUseCases userService;

  @Mock
  private PaymentUseCases paymentService;

  @Nested
  class CreateOrderCheckout {
    @Test
    void shouldCreateCheckoutWithValidRequestSuccessfully() {
      User user = mockUserFactory();

      Product product = mockProductFactory();
      when(productService.findProductById(product.getId())).thenReturn(Optional.of(product));

      OrderItem orderItem = new OrderItem();
      orderItem.setProduct(product);
      orderItem.setQuantity(5);
      orderItem.setUnitPrice(product.getPrice());

      List<OrderItem> items = List.of(orderItem);

      Payment payment = new Payment();
      payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
      payment.setStatus(PaymentStatus.PENDING);
      payment.setPaymentDate(LocalDateTime.now());

      when(paymentService.processPayment(any(Payment.class))).thenReturn(payment);

      CreateOrderCheckoutDTO request = new CreateOrderCheckoutDTO(user, items, PaymentMethod.CREDIT_CARD);

      Order mockOrder = new Order();
      mockOrder.setId(UUID.randomUUID());
      mockOrder.setUser(user);
      mockOrder.setItems(items);
      mockOrder.setPayment(payment);
      mockOrder.setStatus(OrderStatus.PENDING);
      mockOrder.setTotalValue(BigDecimal.valueOf(orderItem.getQuantity()).multiply(product.getPrice()));

      when(orderRepository.saveOrder(any(Order.class))).thenReturn(mockOrder);

      Order order = orderService.createOrderCheckout(request);

      assertNotNull(order, "The order cannot be null!");
      assertEquals(order.getUser(), user);
      assertEquals(order.getItems(), items);
      assertEquals(order.getPayment(), payment);
      assertEquals(OrderStatus.PENDING, order.getStatus());
      assertEquals(0, order.getTotalValue().compareTo(
              BigDecimal.valueOf(orderItem.getQuantity()).multiply(product.getPrice())));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
      CreateOrderCheckoutDTO request = new CreateOrderCheckoutDTO(mockUserFactory(), List.of(), PaymentMethod.CREDIT_CARD);

      assertThrows(IllegalArgumentException.class, () -> orderService.createOrderCheckout(request));
    }
  }

  @Nested
  class GetOrderDetails {
    @Test
    void shouldFetchAllOrderHistorySuccessfully() {
      List<Order> expectedOrders = List.of(mockOrder(), mockOrder());

      when(orderRepository.findAllOrders()).thenReturn(expectedOrders);

      List<Order> orders = orderService.fetchAllOrderHistory();

      assertEquals(expectedOrders, orders);
    }

    @Nested
    class CancelOrder {
      @Test
      void shouldCancelOrderWhenOrderExists() {
        Order mockOrder = mockOrder();
        UUID orderId = mockOrder.getId();
        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.of(mockOrder));

        orderService.cancelOrder(orderId);

        verify(orderRepository).deleteOrder(orderId);

        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.empty());

        Optional<Order> deletedOrder = orderRepository.findOrderById(orderId);
        assertTrue(deletedOrder.isEmpty(), "The order still exists after cancellation!");
      }

      @Test
      void shouldThrowExceptionWhenOrderDoesNotExist() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findOrderById(orderId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
          orderService.cancelOrder(orderId);
        });

        assertEquals("Order cannot be null.", exception.getMessage());

        verify(orderRepository, never()).deleteOrder(any());
      }
    }

    private Order mockOrder() {
      Order order = new Order();
      order.setId(UUID.randomUUID());
      order.setStatus(OrderStatus.PENDING);
      order.setTotalValue(BigDecimal.valueOf(10000.0));
      order.setOrderDate(LocalDateTime.now());
      return order;
    }
  }

  private User mockUserFactory() {
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setName("Jhon Doe");
    user.setEmail("jhondoe@example.com");
    user.setPassword("123");
    user.setRole(UserRole.CUSTOMER_ROLE);
    user.setAddress(new Address());
    user.getAddress().setId(UUID.randomUUID());
    user.getAddress().setStreet("street");
    user.getAddress().setCity("city");
    user.getAddress().setState("state");
    user.getAddress().setZipCode("zipCode");
    user.getAddress().setCountry("country");
    return user;
  }

  private Product mockProductFactory() {
    Product product = new Product();
    product.setId(UUID.randomUUID());
    product.setName("name");
    product.setDescription("description");
    product.setPrice(BigDecimal.valueOf(100.0));
    product.setStock(10);
    product.setCategory(null);
    product.setImagesUrl(null);
    product.setCreatedAt(null);
    return product;
  }
}