package com.application.API_E_commerce.domain.user.dtos;

import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserResponseDTO {

  private UUID id;
  private String name;
  private String email;
  private UserRole role;
  private LocalDateTime createdAt;
  private LocalDateTime lastLoginAt;

  public UserResponseDTO ( UUID id, String name, String email, UserRole role, LocalDateTime createdAt, LocalDateTime lastLoginAt ) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.role = role;
    this.createdAt = createdAt;
    this.lastLoginAt = lastLoginAt;
  }

  public UserResponseDTO ( User user ) {
    this.id = user.getId();
    this.name = user.getName();
    this.email = user.getEmail();
    this.role = user.getRole();
    this.createdAt = user.getCreatedAt();
    this.lastLoginAt = user.getLastLoginAt();
  }

  public void setId ( UUID id ) {
    this.id = id;
  }

  public void setName ( String name ) {
    this.name = name;
  }

  public void setEmail ( String email ) {
    this.email = email;
  }

  public void setRole ( UserRole role ) {
    this.role = role;
  }

  public void setCreatedAt ( LocalDateTime createdAt ) {
    this.createdAt = createdAt;
  }

  public void setLastLoginAt ( LocalDateTime lastLoginAt ) {
    this.lastLoginAt = lastLoginAt;
  }

  public UUID getId () {
    return id;
  }

  public String getName () {
    return name;
  }

  public String getEmail () {
    return email;
  }

  public UserRole getRole () {
    return role;
  }

  public LocalDateTime getCreatedAt () {
    return createdAt;
  }

  public LocalDateTime getLastLoginAt () {
    return lastLoginAt;
  }

}
