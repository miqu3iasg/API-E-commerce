package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.CartUseCases;
import com.application.API_E_commerce.application.usecases.OrderUseCases;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.CartRepository;
import com.application.API_E_commerce.domain.cart.CartStatus;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.cart.cartitem.CartItemRepository;
import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.order.OrderRepository;
import com.application.API_E_commerce.domain.order.OrderStatus;
import com.application.API_E_commerce.domain.order.dtos.CreateOrderCheckoutDTO;
import com.application.API_E_commerce.domain.order.orderitem.OrderItem;
import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.payment.PaymentMethod;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.*;

@Slf4j
@Component
public class CartServiceImplementation implements CartUseCases {
  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final CartItemRepository cartItemRepository;
  private final OrderUseCases orderUseCases;
  private final OrderRepository orderRepository;

  @Autowired
  public CartServiceImplementation (
          CartRepository cartRepository,
          ProductRepository productRepository,
          UserRepository userRepository, CartItemRepository cartItemRepository, OrderUseCases orderUseCases, OrderRepository orderRepository
  ) {
    this.cartRepository = cartRepository;
    this.productRepository = productRepository;
    this.userRepository = userRepository;
    this.cartItemRepository = cartItemRepository;
    this.orderUseCases = orderUseCases;
    this.orderRepository = orderRepository;
  }

  private User validateIfUserExistsAndReturn(final UUID userId) {
    return userRepository
            .findUserById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User cannot be null."));
  }

  private Product validateIfProductExistsAndReturn(final UUID productId) {
    return productRepository
            .findProductById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product cannot be null."));
  }

  private Cart validateIfCartExistsAndReturn(UUID cartId) {
    return this.cartRepository.findCartById(cartId)
            .orElseThrow(() -> new IllegalArgumentException("Cart not found."));
  }

  private void validateInput(final UUID userId, final UUID productId) {
    if (userId == null) throw new IllegalArgumentException("User id cannot be null");
    if (productId == null) throw new IllegalArgumentException("Product id cannot be null");
  }

  private Cart validateUserActiveCart(User user, UUID cartId) {
    if (user.getCarts().isEmpty() || user.getCarts() == null) throw new IllegalArgumentException("User has no cart.");

    return user.getCarts().stream()
            .filter(cart -> cart.getId().equals(cartId) && cart.getCartStatus() == CartStatus.ACTIVE)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("User has no active cart with this id."));
  }

  @Override
  @Transactional
  public Cart addProductToCart(final UUID userId, final UUID productId, int quantity) {
    validateInput(userId, productId);

    if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than 0.");

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
              this.cartItemRepository.saveCartItem(existingItem);
            }, () -> {
              CartItem item = new CartItem(cart, existingProduct, quantity);
              if (cart.getItems() == null) cart.setItems(new ArrayList<>());
              cart.getItems().add(item);
            });

    BigDecimal totalValue = cart.getTotalValue().add(existingProduct.getPrice().multiply(BigDecimal.valueOf(quantity)));
    cart.setTotalValue(totalValue);

    existingProduct.setStock(existingProduct.getStock() - quantity);
    existingProduct.setItems(items);

    this.productRepository.saveProduct(existingProduct);

    return this.cartRepository.saveCart(cart);
  }

  private Cart getOrCreateActiveCart(User user) {
    return user.getCarts().stream()
            .filter(cart -> cart.getCartStatus() == CartStatus.ACTIVE)
            .findFirst()
            .orElseGet(() -> createNewCart(user));
  }

  private Cart createNewCart(User user) {
    Cart newCart = new Cart();
    newCart.setUser(user);
    newCart.setCartStatus(CartStatus.ACTIVE);
    newCart.setCreatedAt(LocalDateTime.now());
    newCart.setItems(new ArrayList<>());
    Cart savedCart = this.cartRepository.saveCart(newCart);
    user.getCarts().add(savedCart);
    return newCart;
  }

  private void validateStockWithExistingQuantities(Product product, int newQuantity, Cart cart) {
    List<CartItem> items = (cart.getItems() != null) ? cart.getItems() : new ArrayList<>();
    int existingQuantity = items
            .stream()
            .filter(item -> item.getProduct().getId().equals(product.getId()))
            .mapToInt(CartItem::getQuantity)
            .sum();

    if (existingQuantity + newQuantity > product.getStock()) {
      throw new IllegalArgumentException("Product is out of stock.");
    }
  }

  @Override
  public void removeProductFromCart(final UUID userId, final UUID productId, final UUID cartId) {
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

    if(quantityRemoved > 0) {
      product.setStock(product.getStock() + quantityRemoved);
      BigDecimal totalValueAfterRemoval = product.getPrice().multiply(BigDecimal.valueOf(quantityRemoved));
      cart.setTotalValue(cart.getTotalValue().subtract(totalValueAfterRemoval));
    }

    cart.setTotalValue(cart.getTotalValue().max(BigDecimal.ZERO));

    this.productRepository.saveProduct(product);

    this.cartRepository.saveCart(cart);
  }

  private static void validatesWhetherThereAreProductsInUserCart(User user) {
    if (user.getCarts().stream()
            .filter(cart -> cart.getCartStatus() == CartStatus.ACTIVE)
            .map(Cart::getItems)
            .mapToInt(List::size)
            .sum() == 0) {
      throw new IllegalArgumentException("Cart has no products.");
    }
  }

  @Override
  public void updateProductQuantityInCart(final UUID userId, final UUID productId, int quantity) {

  }

  @Override
  public void clearCart(final UUID userId) {

  }

  @Override
  public void checkoutCart(final UUID userId, final UUID cartId, PaymentMethod paymentMethod) {
    validateInput(userId, cartId);
    validatePaymentMethod(paymentMethod);

    User user = validateIfUserExistsAndReturn(userId);
    Cart activeCart = validateUserActiveCart(user, cartId);

    if (activeCart.getItems().isEmpty()) {
      throw new IllegalArgumentException("User has no products in cart.");
    }

    List<OrderItem> orderItems = convertCartItemsToOrderItems(activeCart.getItems());

    CreateOrderCheckoutDTO createOrderCheckoutRequest = new CreateOrderCheckoutDTO (
            user, orderItems, paymentMethod
    );

    processOrder(createOrderCheckoutRequest, activeCart);
  }

  @Transactional(rollbackOn = Exception.class)
  private void processOrder(CreateOrderCheckoutDTO createOrderCheckoutRequest, Cart cart) {
    Order order = this.orderUseCases.createOrderCheckout(createOrderCheckoutRequest);

    order.getItems().forEach(item -> item.setOrder(order));
    cart.setCartStatus(CartStatus.COMPLETED);
    cart.getUser().getOrders().add(order);

    this.orderRepository.saveOrder(order);
    this.cartRepository.saveCart(cart);
    this.userRepository.saveUser(cart.getUser());
  }

  private List<OrderItem> convertCartItemsToOrderItems(List<CartItem> cartItems) {
    return cartItems.stream()
            .map(item -> new OrderItem(item.getProduct(), item.getQuantity(), item.getProduct().getPrice()))
            .toList();
  }

  public void validatePaymentMethod(PaymentMethod paymentMethod) {
    if (paymentMethod == null) throw new IllegalArgumentException("Payment method cannot be null.");

    Set<String> validMethods = Set.of (
            PaymentMethod.CREDIT_CARD.toString(),
            PaymentMethod.DEBIT_CARD.toString(),
            PaymentMethod.PAYPAL.toString(),
            PaymentMethod.PIX.toString()
    );

    if (!validMethods.contains(paymentMethod.toString())) {
      throw new IllegalArgumentException("Invalid payment method.");
    }
  }

  @Override
  public List<CartItem> getItemsInCart(UUID userId, UUID cartId) {
    validateInput(userId, cartId);

    User user = validateIfUserExistsAndReturn(userId);
    Cart cart = validateUserActiveCart(user, cartId);

    return Optional.ofNullable(cart.getItems())
            .filter(items -> !items.isEmpty())
            .map(Collections::unmodifiableList)
            .orElseGet(Collections::emptyList);
  }
}
