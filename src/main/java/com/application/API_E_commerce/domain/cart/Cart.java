package com.application.API_E_commerce.domain.cart;

import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cart {
  private UUID id;
  private User user;
  private LocalDateTime createdAt;
  private CartStatus cartStatus;
  private BigDecimal totalValue;
  private List<CartItem> items = new ArrayList<>();

  public Cart() {
  }

  public Cart(UUID id, User user, LocalDateTime createdAt, CartStatus cartStatus, List<CartItem> items) {
    this.id = id;
    this.user = user;
    this.createdAt = createdAt;
    this.cartStatus = cartStatus;
    this.items = items;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public CartStatus getCartStatus() {
    return cartStatus;
  }

  public void setCartStatus(CartStatus cartStatus) {
    this.cartStatus = cartStatus;
  }

  public BigDecimal getTotalValue() {
    return totalValue;
  }

  public void setTotalValue(BigDecimal totalValue) {
    this.totalValue = totalValue;
  }

  public List<CartItem> getItems() {
    return items;
  }

  public void setItems(List<CartItem> items) {
    this.items = items;
  }
}
