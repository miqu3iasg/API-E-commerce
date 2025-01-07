package com.application.API_E_commerce.domain.cart;

import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Cart {
  private UUID id;
  private User user;
  private LocalDateTime createdAt;
  private List<CartItem> items;

  public Cart() {
  }

  public Cart(UUID id, User user, LocalDateTime createdAt, List<CartItem> items) {
    this.id = id;
    this.user = user;
    this.createdAt = createdAt;
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

  public List<CartItem> getItems() {
    return items;
  }

  public void setItems(List<CartItem> items) {
    this.items = items;
  }
}
