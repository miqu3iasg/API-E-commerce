package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.CartRepository;
import com.application.API_E_commerce.domain.cart.CartStatus;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.UserRole;
import com.application.API_E_commerce.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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

  @Test
  @Transactional
  void shouldAddProductInCartWhenUserAndProductExists() {
    Product mockProduct = mockProductFactory();
    mockProduct.setStock(10);

    productRepository.saveProduct(mockProduct);

    User mockUser = mockUserFactory();

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

    Cart cart = new Cart();
    cart.setId(UUID.randomUUID());
    cart.setUser(user);
    cart.setCartStatus(CartStatus.ACTIVE);
    cart.setItems(new ArrayList<>());

    user.setCarts(List.of(cart));

    return user;
  }

  private Product mockProductFactory() {
    Product product = new Product();
    product.setId(UUID.randomUUID());
    product.setName("name");
    product.setDescription("description");
    product.setPrice(BigDecimal.valueOf(100.0));
    product.setCategory(null);
    product.setImagesUrl(null);
    product.setCreatedAt(null);

    return product;
  }
}
