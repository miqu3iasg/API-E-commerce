package com.application.API_E_commerce.domain.user;

import com.application.API_E_commerce.domain.address.Address;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class User {
  private UUID id;
  private String name;
  private String email; // Deve ser validade como email único, e no padrão esperado
  private String password; // Deve ser hash quando for salvar no banco
  private String role; // Customer ou Admin
  private LocalDateTime createdAt;
  private LocalDateTime lastLoginAt;
  private List<String> orders;
  private List<String> carts;
  private Address address;

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
