package com.application.API_E_commerce.domain.order.services;

import com.application.API_E_commerce.adapters.inbound.dtos.CreateOrderCheckoutDTO;
import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.order.OrderStatus;
import com.application.API_E_commerce.domain.order.orderitem.OrderItem;
import com.application.API_E_commerce.domain.order.repository.OrderRepositoryPort;
import com.application.API_E_commerce.domain.order.useCase.OrderUseCase;
import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.payment.PaymentMethod;
import com.application.API_E_commerce.domain.payment.PaymentStatus;
import com.application.API_E_commerce.domain.payment.repository.PaymentRepositoryPort;
import com.application.API_E_commerce.domain.payment.useCase.PaymentUseCase;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.useCase.ProductUseCase;
import com.application.API_E_commerce.domain.stock.useCase.StockUseCase;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.useCase.UserUseCase;
import com.application.API_E_commerce.infrastructure.exceptions.order.EmptyOrderException;
import com.application.API_E_commerce.infrastructure.exceptions.order.OrderNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.payment.MissingPaymentMethodException;
import com.application.API_E_commerce.infrastructure.exceptions.product.ProductNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.user.UserNotFoundException;
import com.stripe.exception.StripeException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService implements OrderUseCase {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	private final OrderRepositoryPort orderRepositoryPort;
	private final UserUseCase userService;
	private final ProductUseCase productService;
	private final PaymentUseCase paymentService;
	private final PaymentRepositoryPort paymentRepositoryPort;
	private final StockUseCase stockService;

	public OrderService (
			OrderRepositoryPort orderRepositoryPort,
			UserUseCase userService,
			ProductUseCase productService,
			PaymentUseCase paymentService,
			PaymentRepositoryPort paymentRepositoryPort,
			StockUseCase stockService
	) {
		this.orderRepositoryPort = orderRepositoryPort;
		this.userService = userService;
		this.productService = productService;
		this.paymentService = paymentService;
		this.paymentRepositoryPort = paymentRepositoryPort;
		this.stockService = stockService;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public Order createOrderCheckout (CreateOrderCheckoutDTO createOrderCheckoutRequest) throws StripeException {
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

		Long amountInCents = totalValue.multiply(BigDecimal.valueOf(100)).longValueExact();

		processPayment(createOrderCheckoutRequest.paymentMethod(), order, amountInCents);

		orderItems.forEach(item -> {
			try {
				Product product = item.getProduct();
				stockService.decreaseProductStock(product.getId(), item.getQuantity());
				product.setStock(item.getProduct().getStock() - item.getQuantity());
			} catch (Exception e) {
				logger.error("Failed to request stock decrease for productId={}, quantity={}: {}",
						item.getProduct().getId(), item.getQuantity(), e.getMessage());
			}
		});

		order.setStatus(OrderStatus.PAYMENT);

		return orderRepositoryPort.saveOrder(order);
	}

	private void validateCreateOrderCheckoutRequest (CreateOrderCheckoutDTO createOrderCheckoutRequest) {
		if (createOrderCheckoutRequest.user() == null)
			throw new UserNotFoundException("User cannot be null");
		if (createOrderCheckoutRequest.items() == null || createOrderCheckoutRequest.items().isEmpty())
			throw new EmptyOrderException("Order must have at least one item");
		if (createOrderCheckoutRequest.paymentMethod() == null)
			throw new MissingPaymentMethodException("Payment method must be specified");
	}

	private void validateProduct (Product product) {
		if (product == null || productService.findProductById(product.getId()).isEmpty())
			throw new ProductNotFoundException("Invalid product");
	}

	private void processPayment (PaymentMethod paymentMethod, Order order, Long amountInCents) throws StripeException {
		Payment payment = new Payment();
		payment.setPaymentMethod(paymentMethod);
		payment.setOrder(order);
		payment.setAmountInCents(amountInCents);
		payment.setDescription(order.getDescription());
		payment.setCurrency(order.getCurrency());
		payment.setStatus(PaymentStatus.PENDING);
		payment.setPaymentDate(LocalDateTime.now());
		paymentService.processPayment(payment);
		paymentRepositoryPort.savePayment(payment);
	}

	@Override
	public List<Order> fetchAllOrderHistory () {
		return orderRepositoryPort.findAllOrders();
	}

	@Override
	public List<Order> fetchOrderHistoryByUser (UUID userId) {
		return userService.findUserById(userId)
				.map(User::getOrders)
				.orElseThrow(UserNotFoundException::new);
	}

	@Override
	public OrderStatus getOrderStatus (UUID orderId) {
		Optional<Order> existingOrder = orderRepositoryPort.findOrderById(orderId);

		if (existingOrder.isEmpty())
			throw new OrderNotFoundException("Order cannot be null.");

		return existingOrder.get().getStatus();
	}

	@Override
	public Optional<Order> findOrderById (UUID orderId) {
		return orderRepositoryPort.findOrderById(orderId);
	}

	@Override
	public void cancelOrder (UUID orderId) {
		orderRepositoryPort.findOrderById(orderId).map(existingOrder -> {
			orderRepositoryPort.deleteOrder(orderId);
			return existingOrder;
		}).orElseThrow(() -> new OrderNotFoundException("Order cannot be null."));
	}

}
