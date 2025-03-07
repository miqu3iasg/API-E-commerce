package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.CartUseCases;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.CartRepository;
import com.application.API_E_commerce.domain.cart.CartStatus;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class CartServiceImplementation implements CartUseCases {
  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;

  public CartServiceImplementation(CartRepository cartRepository, ProductRepository productRepository, UserRepository userRepository) {
    this.cartRepository = cartRepository;
    this.productRepository = productRepository;
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public void addProductToCart(UUID userId, UUID productId, int quantity) {
    validateInput(userId, productId, quantity);

    User user = userRepository
            .findUserById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User cannot be null."));

    Product existingProduct = productRepository
            .findProductById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product cannot be null."));

    Cart cart = getOrCreateActiveCart(user);

    validateStockWithExistingQuantities(existingProduct, quantity, cart);

    cart.getItems()
            .stream()
            .filter(item -> item.getProduct().getId().equals(existingProduct.getId()))
            .findFirst()
            .ifPresentOrElse(existingItem -> {
              existingItem.setQuantity(existingItem.getQuantity() + quantity);
            }, () -> {
              CartItem item = new CartItem();
              item.setProduct(existingProduct);
              item.setQuantity(quantity);
              cart.getItems().add(item);
            });

    cartRepository.saveCart(cart);
  }

  private void validateInput(UUID userId, UUID productId, int quantity) {
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
    cartRepository.saveCart(newCart);
    user.getCarts().add(newCart);
    return newCart;
  }

  private void validateStockWithExistingQuantities(Product product, int newQuantity, Cart cart) {
    int existingQuantity = cart.getItems()
            .stream()
            .filter(item -> item.getProduct().getId().equals(product.getId()))
            .mapToInt(CartItem::getQuantity)
            .sum();

    if (existingQuantity + newQuantity > product.getStock()) {
      throw new IllegalArgumentException("Product is out of stock.");
    }
  }

  @Override
  public void removeProductFromCart(UUID userId, UUID productId) {

  }

  @Override
  public void updateProductQuantityInCart(UUID userId, UUID productId, int quantity) {

  }

  @Override
  public void clearCart(UUID userId) {

  }

  @Override
  public void checkoutCart(UUID userId) {

  }
}
