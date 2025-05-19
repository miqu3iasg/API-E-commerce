package com.application.API_E_commerce.domain.cart.services;

import com.application.API_E_commerce.adapters.inbound.dtos.CreateOrderCheckoutDTO;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.CartStatus;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.cart.cartitem.repository.CartItemRepositoryPort;
import com.application.API_E_commerce.domain.cart.repository.CartRepositoryPort;
import com.application.API_E_commerce.domain.cart.useCase.CartUseCase;
import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.order.orderitem.OrderItem;
import com.application.API_E_commerce.domain.order.repository.OrderRepositoryPort;
import com.application.API_E_commerce.domain.order.useCase.OrderUseCase;
import com.application.API_E_commerce.domain.payment.PaymentMethod;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.repository.ProductRepositoryPort;
import com.application.API_E_commerce.domain.stock.useCase.StockUseCase;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.repository.UserRepositoryPort;
import com.application.API_E_commerce.infrastructure.exceptions.cart.EmptyCartException;
import com.application.API_E_commerce.infrastructure.exceptions.cart.InvalidCartException;
import com.application.API_E_commerce.infrastructure.exceptions.cart.UserCartNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.order.OrderProcessingException;
import com.application.API_E_commerce.infrastructure.exceptions.payment.InvalidPaymentMethodException;
import com.application.API_E_commerce.infrastructure.exceptions.payment.MissingPaymentMethodException;
import com.application.API_E_commerce.infrastructure.exceptions.product.InvalidQuantityException;
import com.application.API_E_commerce.infrastructure.exceptions.product.MissingProductIdException;
import com.application.API_E_commerce.infrastructure.exceptions.product.ProductNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.product.ProductOutOfStockException;
import com.application.API_E_commerce.infrastructure.exceptions.user.MissingUserIdException;
import com.application.API_E_commerce.infrastructure.exceptions.user.UserNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class CartService implements CartUseCase {

	private static final Logger log = LoggerFactory.getLogger(CartService.class);

	private final CartRepositoryPort cartRepositoryPort;
	private final ProductRepositoryPort productRepositoryPort;
	private final UserRepositoryPort userRepositoryPort;
	private final CartItemRepositoryPort cartItemRepositoryPort;
	private final OrderUseCase orderUseCase;
	private final OrderRepositoryPort orderRepositoryPort;
	private final StockUseCase stockService;

	@Autowired
	public CartService (
			CartRepositoryPort cartRepositoryPort,
			ProductRepositoryPort productRepositoryPort,
			UserRepositoryPort userRepositoryPort,
			CartItemRepositoryPort cartItemRepositoryPort,
			OrderUseCase orderUseCase,
			OrderRepositoryPort orderRepositoryPort,
			StockUseCase stockService
	) {
		this.cartRepositoryPort = cartRepositoryPort;
		this.productRepositoryPort = productRepositoryPort;
		this.userRepositoryPort = userRepositoryPort;
		this.cartItemRepositoryPort = cartItemRepositoryPort;
		this.orderUseCase = orderUseCase;
		this.orderRepositoryPort = orderRepositoryPort;
		this.stockService = stockService;
	}

	@Override
	public Cart createCart (UUID userId) {
		if (userId == null)
			throw new MissingUserIdException("User id cannot be null.");

		User user = validateIfUserExistsAndReturn(userId);

		Cart cart = new Cart();
		cart.setUser(user);
		cart.setCartStatus(CartStatus.ACTIVE);
		cart.setCreatedAt(LocalDateTime.now());
		cart.setItems(new ArrayList<>());

		user.getCarts().add(cart);

		return cartRepositoryPort.saveCart(cart);
	}

	private User validateIfUserExistsAndReturn (UUID userId) {
		return userRepositoryPort
				.findUserById(userId)
				.orElseThrow(() -> new UserNotFoundException("User cannot be null."));
	}

	@Override
	@Transactional
	public Cart addProductToCart (UUID userId, UUID productId, int quantity) {
		validateInput(userId, productId);

		if (quantity <= 0)
			throw new InvalidQuantityException("Quantity must be greater than 0.");

		User user = validateIfUserExistsAndReturn(userId);

		Product existingProduct = validateIfProductExistsAndReturn(productId);

		Cart cart = getOrCreateActiveCart(user);

		validateStockWithExistingQuantities(existingProduct, quantity, cart);

		List<CartItem> items = (cart.getItems() != null) ? cart.getItems() : new ArrayList<>();

		items.stream()
				.filter(item -> item.getProduct().getId().equals(existingProduct.getId()))
				.findFirst()
				.ifPresentOrElse(existingItem -> {
					existingItem.setQuantity(existingItem.getQuantity() + quantity);
					cartItemRepositoryPort.saveCartItem(existingItem);
				}, () -> {
					CartItem item = new CartItem(cart, existingProduct, quantity);
					if (cart.getItems() == null) cart.setItems(new ArrayList<>());
					cart.getItems().add(item);
				});

		BigDecimal totalValue = cart.getTotalValue().add(existingProduct.getPrice().multiply(BigDecimal.valueOf(quantity)));
		cart.setTotalValue(totalValue);

		try {
			stockService.decreaseProductStock(existingProduct.getId(), quantity);
		} catch (Exception e) {
			log.error("Failed to restore stock for product when adding to cart {}: " +
					"{}", existingProduct.getId(), e.getMessage());
		}
		existingProduct.setItems(items);

		productRepositoryPort.saveProduct(existingProduct);

		return cartRepositoryPort.saveCart(cart);
	}

	private void validateInput (UUID userId, UUID productId) {
		if (userId == null)
			throw new MissingUserIdException("User id cannot be null");
		if (productId == null)
			throw new MissingProductIdException("Product id cannot be null");
	}

	private Product validateIfProductExistsAndReturn (UUID productId) {
		return productRepositoryPort
				.findProductById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product cannot be null."));
	}

	private Cart getOrCreateActiveCart (User user) {
		return user.getCarts().stream()
				.filter(cart -> cart.getCartStatus() == CartStatus.ACTIVE)
				.findFirst()
				.orElseGet(() -> createNewCart(user));
	}

	private void validateStockWithExistingQuantities (Product product, int newQuantity, Cart cart) {
		List<CartItem> items = (cart.getItems() != null) ? cart.getItems() : new ArrayList<>();
		int existingQuantity = items
				.stream()
				.filter(item -> item.getProduct().getId().equals(product.getId()))
				.mapToInt(CartItem::getQuantity)
				.sum();

		if (existingQuantity + newQuantity > product.getStock())
			throw new ProductOutOfStockException("Product is out of stock.");
	}

	private Cart createNewCart (User user) {
		Cart newCart = new Cart();
		newCart.setUser(user);
		newCart.setCartStatus(CartStatus.ACTIVE);
		newCart.setCreatedAt(LocalDateTime.now());
		newCart.setItems(new ArrayList<>());
		Cart savedCart = cartRepositoryPort.saveCart(newCart);
		user.getCarts().add(savedCart);
		return newCart;
	}

	@Override
	public Cart removeProductFromCart (UUID userId, UUID productId, UUID cartId) {
		validateInput(userId, productId);

		User user = validateIfUserExistsAndReturn(userId);
		Product product = validateIfProductExistsAndReturn(productId);
		Cart cart = validateUserActiveCart(user, cartId);

		validatesWhetherThereAreProductsInUserCart(user);

		List<CartItem> items = new ArrayList<>(cart.getItems());
		cart.setItems(items);

		int quantityRemoved = items.stream()
				.filter(item -> item.getProduct().getId().equals(product.getId()))
				.mapToInt(CartItem::getQuantity)
				.sum();

		items.removeIf(item -> item.getProduct().getId().equals(product.getId()));

		if (quantityRemoved > 0) {
			try {
				stockService.increaseProductStock(product.getId(), quantityRemoved);
			} catch (Exception e) {
				log.error("Failed to restore stock for product when removing from cart {}: {}",
						product.getId(), e.getMessage());
			}
			BigDecimal totalValueAfterRemoval = product.getPrice().multiply(BigDecimal.valueOf(quantityRemoved));
			cart.setTotalValue(cart.getTotalValue().subtract(totalValueAfterRemoval));
		}

		cart.setTotalValue(cart.getTotalValue().max(BigDecimal.ZERO).stripTrailingZeros());

		productRepositoryPort.saveProduct(product);

		return cartRepositoryPort.saveCart(cart);
	}

	private Cart validateUserActiveCart (User user, UUID cartId) {
		if (user.getCarts().isEmpty() || user.getCarts() == null)
			throw new InvalidCartException("User has no cart.");

		return user.getCarts().stream()
				.filter(cart -> cart.getId().equals(cartId) && cart.getCartStatus() == CartStatus.ACTIVE)
				.findFirst()
				.orElseThrow(() -> new UserCartNotFoundException("User has no active " +
						"cart with this id."));
	}

	private static void validatesWhetherThereAreProductsInUserCart (User user) {
		if (user.getCarts().stream()
				.filter(cart -> cart.getCartStatus() == CartStatus.ACTIVE)
				.map(Cart::getItems)
				.mapToInt(List::size)
				.sum() == 0) throw new EmptyCartException("Cart has no products.");
	}

	@Override
	@Transactional
	public void clearCart (UUID userId, UUID cartId) {
		User user = validateIfUserExistsAndReturn(userId);

		Cart cart = validateUserActiveCart(user, cartId);

		for (CartItem item : cart.getItems()) {
			Product product = item.getProduct();
			int quantityToRestore = item.getQuantity();

			try {
				stockService.increaseProductStock(product.getId(), quantityToRestore);
			} catch (Exception e) {
				log.error("Failed to restore stock for product {}: {}", product.getId(), e.getMessage());
			}
		}

		cart.setItems(Collections.emptyList());
		cart.setCartStatus(CartStatus.ABANDONED);
		cart.setTotalValue(BigDecimal.ZERO);

		cartRepositoryPort.saveCart(cart);
	}

	@Override
	public void checkoutCart (UUID userId, UUID cartId, PaymentMethod paymentMethod) {
		validateInput(userId, cartId);
		validatePaymentMethod(paymentMethod);

		User user = validateIfUserExistsAndReturn(userId);
		Cart activeCart = validateUserActiveCart(user, cartId);

		if (activeCart.getItems().isEmpty())
			throw new EmptyCartException("User has no products in cart.");

		List<OrderItem> orderItems = convertCartItemsToOrderItems(activeCart.getItems());

		CreateOrderCheckoutDTO createOrderCheckoutRequest = new CreateOrderCheckoutDTO(
				user, orderItems, paymentMethod
		);

		processOrder(createOrderCheckoutRequest, activeCart);
	}

	private void validatePaymentMethod (PaymentMethod paymentMethod) {
		if (paymentMethod == null)
			throw new MissingPaymentMethodException("Payment method cannot be null.");

		Set<String> validMethods = Set.of(
				PaymentMethod.CARD.toString(),
				PaymentMethod.BOLETO.toString(),
				PaymentMethod.APPLE_PAY.toString(),
				PaymentMethod.GOOGLE_PAY.toString(),
				PaymentMethod.PAYPAL.toString(),
				PaymentMethod.PIX.toString()
		);

		if (! validMethods.contains(paymentMethod.toString()))
			throw new InvalidPaymentMethodException("Invalid payment method.");
	}

	private List<OrderItem> convertCartItemsToOrderItems (List<CartItem> cartItems) {
		return cartItems.stream()
				.map(item -> new OrderItem(item.getProduct(), item.getQuantity(), item.getProduct().getPrice()))
				.toList();
	}

	@Transactional(rollbackOn = Exception.class)
	private void processOrder (CreateOrderCheckoutDTO createOrderCheckoutRequest, Cart cart) {
		try {
			Order order = orderUseCase.createOrderCheckout(createOrderCheckoutRequest);

			order.getItems().forEach(item -> item.setOrder(order));
			cart.setCartStatus(CartStatus.COMPLETED);
			cart.getUser().getOrders().add(order);

			orderRepositoryPort.saveOrder(order);
			cartRepositoryPort.saveCart(cart);
			userRepositoryPort.saveUser(cart.getUser());
		} catch (Exception e) {
			throw new OrderProcessingException("An error occurred while processing " +
					"the " + "order: " + e.getMessage(), e);
		}
	}

	@Override
	public List<CartItem> getItemsInCart (UUID userId, UUID cartId) {
		validateInput(userId, cartId);

		User user = validateIfUserExistsAndReturn(userId);
		Cart cart = validateUserActiveCart(user, cartId);

		return Optional.ofNullable(cart.getItems())
				.filter(items -> ! items.isEmpty())
				.map(Collections::unmodifiableList)
				.orElseGet(Collections::emptyList);
	}

}
