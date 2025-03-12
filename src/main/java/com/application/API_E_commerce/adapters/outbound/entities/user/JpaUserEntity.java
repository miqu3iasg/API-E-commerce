package com.application.API_E_commerce.adapters.outbound.entities.user;

import com.application.API_E_commerce.adapters.outbound.entities.address.JpaAddressEntity;
import com.application.API_E_commerce.adapters.outbound.entities.cart.JpaCartEntity;
import com.application.API_E_commerce.adapters.outbound.entities.order.JpaOrderEntity;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.UserRole;
import com.application.API_E_commerce.utils.email.EmailValidation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_users")
@AllArgsConstructor
@NoArgsConstructor
public class JpaUserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private String name;

  @Column(unique = true, nullable = false)
  @EmailValidation
  private String email;

  private String password;

  private UserRole role;

  private LocalDateTime createdAt;

  private LocalDateTime lastLoginAt;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JpaOrderEntity> orders = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JpaCartEntity> carts = new ArrayList<>();

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "address_id")
  private JpaAddressEntity address;

  public static JpaUserEntity fromDomain (
          User domain,
          List<JpaOrderEntity> jpaOrders,
          List<JpaCartEntity> jpaCarts,
          JpaAddressEntity jpaAddress
  ) {
    JpaUserEntity jpaUserEntity = new JpaUserEntity();

    jpaUserEntity.id = domain.getId();
    jpaUserEntity.name = domain.getName();
    jpaUserEntity.email = domain.getEmail();
    jpaUserEntity.password = domain.getPassword();
    jpaUserEntity.role = domain.getRole();
    jpaUserEntity.createdAt = domain.getCreatedAt();
    jpaUserEntity.lastLoginAt = domain.getLastLoginAt();
    jpaUserEntity.orders = jpaOrders;
    jpaUserEntity.carts = jpaCarts;
    jpaUserEntity.address = jpaAddress;

    return jpaUserEntity;
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

  public List<JpaOrderEntity> getOrders() {
    return orders;
  }

  public void setOrders(List<JpaOrderEntity> orders) {
    this.orders = orders;
  }

  public List<JpaCartEntity> getCarts() {
    return carts;
  }

  public void setCarts(List<JpaCartEntity> carts) {
    this.carts = carts;
  }

  public JpaAddressEntity getAddress() {
    return address;
  }

  public void setAddress(JpaAddressEntity address) {
    this.address = address;
  }
}
