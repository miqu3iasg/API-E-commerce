package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.CartStatus;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.cart.repository.CartRepositoryPort;
import com.application.API_E_commerce.domain.cart.services.CartService;
import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.order.orderitem.OrderItem;
import com.application.API_E_commerce.domain.order.repository.OrderRepositoryPort;
import com.application.API_E_commerce.domain.order.useCase.OrderUseCase;
import com.application.API_E_commerce.domain.payment.PaymentMethod;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.repository.ProductRepositoryPort;
import com.application.API_E_commerce.domain.product.useCase.ProductUseCase;
import com.application.API_E_commerce.domain.stock.useCase.StockUseCase;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.UserRole;
import com.application.API_E_commerce.domain.user.repository.UserRepositoryPort;
import com.application.API_E_commerce.infrastructure.exceptions.cart.EmptyCartException;
import com.application.API_E_commerce.infrastructure.exceptions.cart.InvalidCartException;
import com.application.API_E_commerce.infrastructure.exceptions.payment.MissingPaymentMethodException;
import com.application.API_E_commerce.infrastructure.exceptions.product.InvalidQuantityException;
import com.application.API_E_commerce.infrastructure.exceptions.product.MissingProductIdException;
import com.application.API_E_commerce.infrastructure.exceptions.product.ProductNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.user.MissingUserIdException;
import com.application.API_E_commerce.infrastructure.exceptions.user.UserNotFoundException;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

	@InjectMocks
	private CartService cartService;

	@Mock
	private CartRepositoryPort cartRepositoryPort;

	@Mock
	private ProductRepositoryPort productRepositoryPort;

	@Mock
	private UserRepositoryPort userRepositoryPort;

	@Mock
	private OrderUseCase orderUseCase;

	@Mock
	private OrderRepositoryPort orderRepositoryPort;

	@Mock
	private ProductUseCase productService;

	@Mock
	private StockUseCase stockUseCase;

	private static Order mockOrderFactory (Cart cart, User user) {
		List<OrderItem> orderItems = cart.getItems().stream()
				.map(item -> new OrderItem(item.getProduct(), item.getQuantity(), item.getProduct().getPrice()))
				.toList();

		Order order = new Order();
		order.setId(UUID.randomUUID());
		order.setUser(user);
		order.setItems(orderItems);
		return order;
	}

	private static Cart mockCartFactory (User user, Product product) {
		Cart cart = new Cart();
		cart.setId(UUID.randomUUID());
		cart.setUser(user);
		cart.setCartStatus(CartStatus.ACTIVE);

		if (product != null) {
			CartItem item = new CartItem();
			item.setId(UUID.randomUUID());
			item.setCart(cart);
			item.setProduct(product);
			item.setQuantity(2);
			cart.setTotalValue(product.getPrice().multiply(BigDecimal.valueOf(2)));
			cart.setItems(List.of(item));
		} else cart.setItems(new ArrayList<>());

		user.setCarts(List.of(cart));
		return cart;
	}

	private static User mockUserFactory () {
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

	private static Product mockProductFactory () {
		Product product = new Product();
		product.setId(UUID.randomUUID());
		product.setName("name");
		product.setDescription("description");
		product.setStock(10);
		product.setPrice(BigDecimal.valueOf(100.0));
		product.setCategory(null);
		product.setImagesUrl(null);
		product.setCreatedAt(null);
		return product;
	}

	@Nested
	class CreateCart {

		@Test
		void shouldCreateCartSuccessfullyWithValidRequest () {
			// Arrange
			User user = mockUserFactory();
			when(userRepositoryPort.findUserById(user.getId())).thenReturn(Optional.of(user));
			when(cartRepositoryPort.saveCart(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0, Cart.class));

			// Act
			Cart cart = cartService.createCart(user.getId());

			// Assert
			assertNotNull(cart);
			assertNotNull(cart.getUser());
			assertNotNull(user.getCarts());
			assertEquals(CartStatus.ACTIVE, cart.getCartStatus());
			assertEquals(CartStatus.ACTIVE, user.getCarts().getFirst().getCartStatus());
			assertEquals(Collections.emptyList(), cart.getItems(), "Should return an empty list.");
			assertEquals(0, cart.getItems().size(), "The size of cart items list should be zero.");
			verify(stockUseCase, never()).decreaseProductStock(any(), anyInt());
			verify(stockUseCase, never()).increaseProductStock(any(), anyInt());
		}

	}

	@Nested
	class AddProductToCart {

		@Test
		void shouldAddProductInCartWhenUserAndProductExists () {
			// Arrange
			Product product = mockProductFactory();
			User user = mockUserFactory();
			Cart cart = new Cart();
			cart.setId(UUID.randomUUID());
			cart.setUser(user);
			cart.setCartStatus(CartStatus.ACTIVE);
			cart.setItems(new ArrayList<>());
			cart.setTotalValue(BigDecimal.ZERO);
			user.setCarts(List.of(cart));

			when(userRepositoryPort.findUserById(user.getId())).thenReturn(Optional.of(user));
			when(productRepositoryPort.findProductById(product.getId())).thenReturn(Optional.of(product));
			when(cartRepositoryPort.saveCart(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

			// Act
			Cart result = cartService.addProductToCart(user.getId(), product.getId(), 5);

			// Assert
			assertNotNull(result);
			assertNotNull(result.getItems());
			assertEquals(1, result.getItems().size());
			assertEquals(product, result.getItems().getFirst().getProduct());
			assertEquals(5, result.getItems().getFirst().getQuantity());
			assertEquals(CartStatus.ACTIVE, result.getCartStatus());
			assertEquals(user, result.getUser());
			assertNotNull(result.getId());
			verify(stockUseCase).decreaseProductStock(product.getId(), 5);
		}

		@Test
		void shouldAddProductEvenIfStockUpdateFails () {
			// Arrange
			Product product = mockProductFactory();
			User user = mockUserFactory();
			Cart cart = new Cart();
			cart.setId(UUID.randomUUID());
			cart.setUser(user);
			cart.setCartStatus(CartStatus.ACTIVE);
			cart.setItems(new ArrayList<>());
			cart.setTotalValue(BigDecimal.ZERO);
			user.setCarts(List.of(cart));

			when(userRepositoryPort.findUserById(user.getId())).thenReturn(Optional.of(user));
			when(productRepositoryPort.findProductById(product.getId())).thenReturn(Optional.of(product));
			when(cartRepositoryPort.saveCart(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
			doThrow(new InvalidQuantityException("Quantity to decrease must be " +
					"greater than zero."))
					.when(stockUseCase).decreaseProductStock(product.getId(), 5);

			// Act
			Cart result = cartService.addProductToCart(user.getId(), product.getId(), 5);

			// Assert
			assertNotNull(result);
			assertNotNull(result.getItems());
			assertEquals(1, result.getItems().size());
			assertEquals(product, result.getItems().getFirst().getProduct());
			assertEquals(5, result.getItems().getFirst().getQuantity());
			verify(stockUseCase).decreaseProductStock(product.getId(), 5);
		}

	}

	@Nested
	class RemoveProductFromCart {

		@Test
		void shouldRemoveProductFromCartWhenUserAndProductExists () {
			// Arrange
			User user = mockUserFactory();
			Product product = mockProductFactory();
			Cart cart = mockCartFactory(user, product);

			when(userRepositoryPort.findUserById(user.getId())).thenReturn(Optional.of(user));
			when(productRepositoryPort.findProductById(product.getId())).thenReturn(Optional.of(product));
			when(cartRepositoryPort.saveCart(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
			when(productRepositoryPort.saveProduct(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

			// Act
			cartService.removeProductFromCart(user.getId(), product.getId(), cart.getId());

			// Assert
			assertTrue(cart.getItems().isEmpty(), "The cart should be empty after product removal");
			assertTrue(cart.getTotalValue().compareTo(BigDecimal.ZERO) == 0, "The " +
					"total value should be zero after product removal");
			assertEquals(user, cart.getUser(), "The cart should belong to the correct user");
			verify(stockUseCase).increaseProductStock(product.getId(), 2); // Quantidade do item no mockCartFactory
		}

		@Test
		void shouldRemoveProductEvenIfStockUpdateFails () {
			// Arrange
			User user = mockUserFactory();
			Product product = mockProductFactory();
			Cart cart = mockCartFactory(user, product);

			when(userRepositoryPort.findUserById(user.getId())).thenReturn(Optional.of(user));
			when(productRepositoryPort.findProductById(product.getId())).thenReturn(Optional.of(product));
			when(cartRepositoryPort.saveCart(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
			when(productRepositoryPort.saveProduct(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
			doThrow(new InvalidQuantityException("Quantity to increase must be greater than zero."))
					.when(stockUseCase).increaseProductStock(product.getId(), 2);

			// Act
			cartService.removeProductFromCart(user.getId(), product.getId(), cart.getId());

			// Assert
			assertTrue(cart.getItems().isEmpty(), "The cart should be empty after product removal");
			assertTrue(cart.getTotalValue().compareTo(BigDecimal.ZERO) == 0, "The " +
					"total value should be zero after product removal");
			verify(stockUseCase).increaseProductStock(product.getId(), 2);
		}

		@Test
		void shouldThrowExceptionIfInputIsInvalid () {
			// Arrange
			Product product = mockProductFactory();
			User user = mockUserFactory();
			Cart cart = mockCartFactory(user, product);

			// Act & Assert
			assertThrows(MissingUserIdException.class,
					() -> cartService.removeProductFromCart(null, product.getId(), cart.getId()));
			assertThrows(MissingProductIdException.class,
					() -> cartService.removeProductFromCart(user.getId(), null, cart.getId()));
			assertThrows(UserNotFoundException.class,
					() -> cartService.removeProductFromCart(user.getId(), product.getId(), null));
			verify(stockUseCase, never()).increaseProductStock(any(), anyInt());
		}

		@Test
		void shouldThrowExceptionIfCartDoesNotExist () {
			// Arrange
			Product product = mockProductFactory();
			User user = mockUserFactory();
			UUID cartId = UUID.randomUUID();

			// Act & Assert
			assertThrows(UserNotFoundException.class,
					() -> cartService.removeProductFromCart(user.getId(), product.getId(), cartId));
			verify(stockUseCase, never()).increaseProductStock(any(), anyInt());
		}

		@Test
		void shouldThrowExceptionIfProductIsNotInCart () {
			// Arrange
			User user = mockUserFactory();
			Product product = mockProductFactory();
			Cart cart = mockCartFactory(user, null);

			when(userRepositoryPort.findUserById(user.getId())).thenReturn(Optional.of(user));

			// Act & Assert
			assertThrows(ProductNotFoundException.class,
					() -> cartService.removeProductFromCart(user.getId(), product.getId(), cart.getId()));
			verify(stockUseCase, never()).increaseProductStock(any(), anyInt());
		}

	}

	@Nested
	class CheckoutCart {

		@Test
		void shouldThrowExceptionWhenUserIdIsNull () {
			// Arrange
			UUID cartId = UUID.randomUUID();
			final PaymentMethod validPaymentMethod = PaymentMethod.CARD;

			// Act & Assert
			assertThrows(MissingUserIdException.class,
					() -> cartService.checkoutCart(null, cartId, validPaymentMethod));
			verify(stockUseCase, never()).decreaseProductStock(any(), anyInt());
		}

		@Test
		void shouldThrowExceptionWhenCartIdIsNull () {
			// Arrange
			UUID userId = UUID.randomUUID();
			final PaymentMethod validPaymentMethod = PaymentMethod.CARD;

			// Act & Assert
			assertThrows(MissingProductIdException.class,
					() -> cartService.checkoutCart(userId, null, validPaymentMethod));
			verify(stockUseCase, never()).decreaseProductStock(any(), anyInt());
		}

		@Test
		void shouldThrowExceptionWhenPaymentMethodIsNull () {
			// Arrange
			UUID userId = UUID.randomUUID();
			UUID cartId = UUID.randomUUID();

			// Act & Assert
			assertThrows(MissingPaymentMethodException.class,
					() -> cartService.checkoutCart(userId, cartId, null));
			verify(stockUseCase, never()).decreaseProductStock(any(), anyInt());
		}

		@Test
		void shouldThrowExceptionWhenPaymentMethodIsInvalid () {
			// Arrange
			UUID userId = UUID.randomUUID();
			UUID cartId = UUID.randomUUID();

			// Act & Assert
			assertThrows(IllegalArgumentException.class,
					() -> cartService.checkoutCart(userId, cartId, PaymentMethod.valueOf("BITCOIN")));
			verify(stockUseCase, never()).decreaseProductStock(any(), anyInt());
		}

		@Test
		void shouldThrowExceptionWhenUserDoesNotExist () {
			// Arrange
			UUID userId = UUID.randomUUID();
			UUID cartId = UUID.randomUUID();
			final PaymentMethod validPaymentMethod = PaymentMethod.CARD;

			// Act & Assert
			assertThrows(UserNotFoundException.class,
					() -> cartService.checkoutCart(userId, cartId, validPaymentMethod));
			verify(stockUseCase, never()).decreaseProductStock(any(), anyInt());
		}

		@Test
		void shouldThrowExceptionWhenCartDoesNotExist () {
			// Arrange
			UUID cartId = UUID.randomUUID();
			final PaymentMethod validPaymentMethod = PaymentMethod.CARD;
			User user = mockUserFactory();

			when(userRepositoryPort.findUserById(user.getId())).thenReturn(Optional.of(user));

			// Act & Assert
			assertThrows(InvalidCartException.class,
					() -> cartService.checkoutCart(user.getId(), cartId, validPaymentMethod));
			verify(stockUseCase, never()).decreaseProductStock(any(), anyInt());
		}

		@Test
		void shouldThrowExceptionWhenCartIsEmpty () {
			// Arrange
			final PaymentMethod validPaymentMethod = PaymentMethod.CARD;
			User user = mockUserFactory();
			Cart cart = mockCartFactory(user, null);
			cart.setItems(List.of());

			when(userRepositoryPort.findUserById(user.getId())).thenReturn(Optional.of(user));

			// Act & Assert
			assertThrows(EmptyCartException.class,
					() -> cartService.checkoutCart(user.getId(), cart.getId(), validPaymentMethod));
			verify(stockUseCase, never()).decreaseProductStock(any(), anyInt());
		}

		@Test
		void shouldProcessCheckoutSuccessfully () throws StripeException {
			// Arrange
			final PaymentMethod validPaymentMethod = PaymentMethod.CARD;
			User user = mockUserFactory();
			Product product = mockProductFactory();
			Cart cart = mockCartFactory(user, product);
			Order order = mockOrderFactory(cart, user);

			when(userRepositoryPort.findUserById(user.getId())).thenReturn(Optional.of(user));
			when(orderUseCase.createOrderCheckout(any())).thenReturn(order);
			when(cartRepositoryPort.saveCart(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

			// Act
			cartService.checkoutCart(user.getId(), cart.getId(), validPaymentMethod);

			// Assert
			assertEquals(CartStatus.COMPLETED, cart.getCartStatus());
			assertFalse(user.getOrders().isEmpty(), "User should have at least one order after checkout");
			assertNotNull(cart.getId(), "The cart id should not be null");
			assertEquals(user, cart.getUser(), "The cart should belong to the correct user");
			assertFalse(cart.getItems().isEmpty(), "The cart should not be empty");
			assertEquals(product, cart.getItems().getFirst().getProduct(), "The product in the cart should match");
			verify(stockUseCase, never()).decreaseProductStock(any(), anyInt()); // Estoque já reservado em addProductToCart
		}

	}

	@Nested
	class ListItemsInCart {

		@Test
		void shouldReturnCartItemsWhenValidInput () {
			// Arrange
			Product product = mockProductFactory();
			User user = mockUserFactory();
			Cart cart = mockCartFactory(user, product);

			when(userRepositoryPort.findUserById(user.getId())).thenReturn(Optional.of(user));

			// Act
			List<CartItem> itemsInCart = cartService.getItemsInCart(user.getId(), cart.getId());

			// Assert
			assertAll(
					() -> assertNotNull(itemsInCart, "The result should not be null"),
					() -> assertFalse(itemsInCart.isEmpty(), "The result should not be empty"),
					() -> assertEquals(cart.getItems().size(), itemsInCart.size(), "The result should have the same size as the cart items"),
					() -> assertIterableEquals(cart.getItems(), itemsInCart, "The result should be the same as the cart items"),
					() -> assertThrows(UnsupportedOperationException.class, () -> itemsInCart.add(new CartItem()),
							"The result should be unmodifiable")
			);
			verify(stockUseCase, never()).decreaseProductStock(any(), anyInt());
			verify(stockUseCase, never()).increaseProductStock(any(), anyInt());
		}

		@Test
		void shouldThrowWhenIdsAreNull () {
			// Act & Assert
			assertThrows(MissingUserIdException.class,
					() -> cartService.getItemsInCart(null, UUID.randomUUID()));
			assertThrows(MissingProductIdException.class,
					() -> cartService.getItemsInCart(UUID.randomUUID(), null));
			verify(stockUseCase, never()).decreaseProductStock(any(), anyInt());
			verify(stockUseCase, never()).increaseProductStock(any(), anyInt());
		}

		@Test
		void shouldReturnEmptyListForCartWithEmptyItems () {
			// Arrange
			User user = mockUserFactory();
			Product product = mockProductFactory();
			Cart cart = mockCartFactory(user, product);
			cart.setItems(List.of());

			when(userRepositoryPort.findUserById(user.getId())).thenReturn(Optional.of(user));

			// Act & Assert
			assertTrue(cartService.getItemsInCart(user.getId(), cart.getId()).isEmpty());
			verify(stockUseCase, never()).decreaseProductStock(any(), anyInt());
			verify(stockUseCase, never()).increaseProductStock(any(), anyInt());
		}

	}

	@Nested
	class ClearCart {

		@Test
		void shouldRemoveItemsInCartWithCartAndUserExists () {
			// Arrange
			User user = mockUserFactory();
			Product product = mockProductFactory();
			Cart cart = mockCartFactory(user, product);

			when(userRepositoryPort.findUserById(user.getId())).thenReturn(Optional.of(user));
			when(cartRepositoryPort.saveCart(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

			// Act
			cartService.clearCart(user.getId(), cart.getId());

			// Assert
			assertNotNull(cart);
			assertNotNull(cart.getId());
			assertNotNull(cart.getUser());
			assertEquals(CartStatus.ABANDONED, cart.getCartStatus());
			assertEquals(Collections.emptyList(), cart.getItems(), "Should return an empty list.");
			assertEquals(0, cart.getItems().size(), "The size of cart items list should be zero.");
			verify(stockUseCase).increaseProductStock(product.getId(), 2); // Quantidade do item no mockCartFactory
		}

		@Test
		void shouldClearCartEvenIfStockUpdateFails () {
			// Arrange
			User user = mockUserFactory();
			Product product = mockProductFactory();
			Cart cart = mockCartFactory(user, product);

			when(userRepositoryPort.findUserById(user.getId())).thenReturn(Optional.of(user));
			when(cartRepositoryPort.saveCart(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

			doThrow(new InvalidQuantityException("Quantity to increase must be greater than zero."))
					.when(stockUseCase).increaseProductStock(product.getId(), 2);

			// Act
			cartService.clearCart(user.getId(), cart.getId());

			// Assert
			assertNotNull(cart);
			assertNotNull(cart.getId());
			assertNotNull(cart.getUser());
			assertEquals(CartStatus.ABANDONED, cart.getCartStatus());
			assertEquals(Collections.emptyList(), cart.getItems(), "Should return an empty list.");
			assertEquals(0, cart.getItems().size(), "The size of cart items list should be zero.");
			verify(stockUseCase).increaseProductStock(product.getId(), 2);
		}

	}

}