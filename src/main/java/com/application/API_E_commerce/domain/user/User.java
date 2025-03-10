package com.application.API_E_commerce.domain.user;

import com.application.API_E_commerce.adapters.outbound.entities.user.JpaUserEntity;
import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.order.Order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
  private UUID id;
  private String name;
  private String email;
  private String password;
  private UserRole role;
  private LocalDateTime createdAt;
  private LocalDateTime lastLoginAt;
  private List<Order> orders;
  private List<Cart> carts = new ArrayList<>();
  private Address address;

  public User() {
  }

  public User (
    UUID id, 
    String name, 
    String email, 
    String password, 
    UserRole role, 
    LocalDateTime createdAt, 
    LocalDateTime lastLoginAt, 
    List<Order> orders, 
    List<Cart> carts, 
    Address address
    ) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.role = role;
    this.createdAt = createdAt;
    this.lastLoginAt = lastLoginAt;
    this.orders = orders;
    this.carts = carts;
    this.address = address;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }

  public List<Order> getOrders() {
    return orders;
  }

  public void setOrders(List<Order> orders) {
    this.orders = orders;
  }

  public List<Cart> getCarts() {
    return carts;
  }

  public void setCarts(List<Cart> carts) {
    this.carts = carts;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getLastLoginAt() {
    return lastLoginAt;
  }

  public void setLastLoginAt(LocalDateTime lastLoginAt) {
    this.lastLoginAt = lastLoginAt;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }
}
