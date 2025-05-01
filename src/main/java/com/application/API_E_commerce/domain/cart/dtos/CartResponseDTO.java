package com.application.API_E_commerce.domain.cart.dtos;

import com.application.API_E_commerce.domain.cart.CartStatus;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.user.dtos.UserResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CartResponseDTO {

  private UUID id;
  private UserResponseDTO user;
  private LocalDateTime createdAt;
  private CartStatus cartStatus;
  private BigDecimal totalValue;
  private List<CartItem> items;

  public CartResponseDTO ( UUID id, UserResponseDTO user, LocalDateTime createdAt, CartStatus cartStatus, BigDecimal totalValue, List<CartItem> items ) {
    this.id = id;
    this.user = user;
    this.createdAt = createdAt;
    this.cartStatus = cartStatus;
    this.totalValue = totalValue;
    this.items = items;
  }

  public UUID getId () {
    return id;
  }

  public void setId ( UUID id ) {
    this.id = id;
  }

  public UserResponseDTO getUser () {
    return user;
  }

  public void setUser ( UserResponseDTO user ) {
    this.user = user;
  }

  public LocalDateTime getCreatedAt () {
    return createdAt;
  }

  public void setCreatedAt ( LocalDateTime createdAt ) {
    this.createdAt = createdAt;
  }

  public CartStatus getCartStatus () {
    return cartStatus;
  }

  public void setCartStatus ( CartStatus cartStatus ) {
    this.cartStatus = cartStatus;
  }

  public BigDecimal getTotalValue () {
    return totalValue;
  }

  public void setTotalValue ( BigDecimal totalValue ) {
    this.totalValue = totalValue;
  }

  public List<CartItem> getItems () {
    return items;
  }

  public void setItems ( List<CartItem> items ) {
    this.items = items;
  }

}
