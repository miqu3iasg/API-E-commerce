package com.application.API_E_commerce.adapters.outbound.entities.cart;

import com.application.API_E_commerce.adapters.outbound.entities.user.JpaUserEntity;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.CartStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_carts")
@AllArgsConstructor
@NoArgsConstructor
public class JpaCartEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private JpaUserEntity user;

  private LocalDateTime createdAt;

  private CartStatus cartStatus;

  private BigDecimal totalValue;

  @OneToMany(mappedBy = "cart", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, orphanRemoval = true)
  private List<JpaCartItemEntity> items = new ArrayList<>();

  @PrePersist
  public void generateUUID() {
    if (id == null) {
      id = UUID.randomUUID();
    }
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public JpaUserEntity getUser() {
    return user;
  }

  public void setUser(JpaUserEntity user) {
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

  public List<JpaCartItemEntity> getItems() {
    return items;
  }

  public void setItems(List<JpaCartItemEntity> items) {
    this.items = items;
  }
}
