package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.CartRepository;
import com.application.API_E_commerce.domain.cart.CartStatus;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.UserRole;
import com.application.API_E_commerce.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

  @InjectMocks
  private CartServiceImplementation cartService;

  @Mock
  private CartRepository cartRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private UserRepository userRepository;

  @Nested
  class AddProductToCart {
    @Test
    @Transactional
    void shouldAddProductInCartWhenUserAndProductExists() {
      Product mockProduct = mockProductFactory();
      mockProduct.setStock(10);

      productRepository.saveProduct(mockProduct);

      User mockUser = mockUserFactory();

      Cart mockCart = new Cart();
      mockCart.setId(UUID.randomUUID());
      mockCart.setUser(mockUser);
      mockCart.setCartStatus(CartStatus.ACTIVE);
      mockCart.setItems(new ArrayList<>());
      mockCart.setTotalValue(BigDecimal.valueOf(1000.0));

      mockUser.setCarts(List.of(mockCart));

      userRepository.saveUser(mockUser);

      when(userRepository.findUserById(mockUser.getId())).thenReturn(Optional.of(mockUser));
      when(productRepository.findProductById(mockProduct.getId())).thenReturn(Optional.of(mockProduct));

      when(cartRepository.saveCart(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

      Cart cart = cartService.addProductToCart(mockUser.getId(), mockProduct.getId(), 5);

      assertNotNull(cart);
      assertEquals(5, mockProduct.getStock());
      assertNotNull(cart.getItems());
      assertEquals(1, cart.getItems().size());
      assertEquals(mockProduct, cart.getItems().getFirst().getProduct());
      assertEquals(5, cart.getItems().getFirst().getQuantity());
      assertEquals(CartStatus.ACTIVE, cart.getCartStatus());
      assertEquals(mockUser, cart.getUser());
      assertNotNull(cart.getId());
    }
  }

  @Nested
  class RemoveProductFromCart {
    @Test
    void shouldRemoveProductFromCartWhenUserAndProductExists() {
      User user = mockUserFactory();

      Product product = mockProductFactory();
      product.setStock(10);

      Cart userCart = mockCartFactory(user, product);

      when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));
      when(productRepository.findProductById(product.getId())).thenReturn(Optional.of(product));
      when(cartRepository.saveCart(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
      when(productRepository.saveProduct(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

      cartService.removeProductFromCart(user.getId(), product.getId());

      assertTrue(userCart.getItems().isEmpty(), "The cart should be empty after product removal");
      assertEquals(new BigDecimal("0.0"), userCart.getTotalValue(), "The total value should be zero after product removal");
      assertEquals(12, product.getStock(), "The product stock should be updated correctly after removal");
      assertEquals(user, userCart.getUser(), "The cart should belong to the correct user");
    }

    @Test
    void shouldThrowExceptionIfInputIsInvalid() {
      Product product = mockProductFactory();
      User user = mockUserFactory();
      assertThrows(IllegalArgumentException.class, () -> cartService.removeProductFromCart(null, product.getId()));
      assertThrows(IllegalArgumentException.class, () -> cartService.removeProductFromCart(user.getId(), null));
    }

    @Test
    void shouldThrowExceptionIfCartDoesNotExist() {
      Product product = mockProductFactory();
      User user = mockUserFactory();
      assertThrows(IllegalArgumentException.class, () -> cartService.removeProductFromCart(user.getId(), product.getId()));
    }

    @Test
    void shouldThrowExceptionIfProductDoesNotExist() {
      UUID productId = UUID.randomUUID();
      User user = mockUserFactory();

      assertThrows(IllegalArgumentException.class, () -> cartService.removeProductFromCart(user.getId(), productId));
    }

    @Test
    void shouldThrowExceptionIfProductIsNotInCart() {
      User user = mockUserFactory();
      Product product = mockProductFactory();
      Cart cart = mockCartFactory(user, null);

      when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));

      assertThrows(IllegalArgumentException.class, () -> cartService.removeProductFromCart(user.getId(), product.getId()));
    }
  }

  private Cart mockCartFactory(User user, Product product) {
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
    }
    else cart.setItems(new ArrayList<>());

    user.setCarts(List.of(cart));

    return cart;
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
    product.setStock(10);
    product.setPrice(BigDecimal.valueOf(100.0));
    product.setCategory(null);
    product.setImagesUrl(null);
    product.setCreatedAt(null);

    return product;
  }
}
