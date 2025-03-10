package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.adapters.outbound.repositories.CartRepositoryImplementation;
import com.application.API_E_commerce.application.usecases.CartUseCases;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.CartRepository;
import com.application.API_E_commerce.domain.cart.CartStatus;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.cart.cartitem.CartItemRepository;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CartServiceImplementation implements CartUseCases {
  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final CartItemRepository cartItemRepository;

  @Autowired
  public CartServiceImplementation (
          CartRepository cartRepository,
          ProductRepository productRepository,
          UserRepository userRepository, CartItemRepository cartItemRepository
  ) {
    this.cartRepository = cartRepository;
    this.productRepository = productRepository;
    this.userRepository = userRepository;
    this.cartItemRepository = cartItemRepository;
  }

  @Override
  @Transactional
  public Cart addProductToCart(final UUID userId, final UUID productId, int quantity) {
    validateInput(userId, productId, quantity);

    User user = userRepository
            .findUserById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User cannot be null."));

    Product existingProduct = productRepository
            .findProductById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product cannot be null."));

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
              CartItem item = new CartItem();
              item.setProduct(existingProduct);
              item.setQuantity(quantity);
              item.setCart(cart);
              this.cartItemRepository.saveCartItem(item);
              if (cart.getItems() == null) cart.setItems(new ArrayList<>());
              cart.getItems().add(item);
            });

    existingProduct.setStock(existingProduct.getStock() - quantity);

    this.productRepository.saveProduct(existingProduct);

    return this.cartRepository.saveCart(cart);
  }

  private void validateInput(final UUID userId, final UUID productId, int quantity) {
    if (userId == null) throw new IllegalArgumentException("User id cannot be null");
    if (productId == null) throw new IllegalArgumentException("Product id cannot be null");
    if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than 0.");
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
  public void removeProductFromCart(final UUID userId, final UUID productId) {

  }

  @Override
  public void updateProductQuantityInCart(final UUID userId, final UUID productId, int quantity) {

  }

  @Override
  public void clearCart(final UUID userId) {

  }

  @Override
  public void checkoutCart(final UUID userId) {

  }
}
