package com.application.API_E_commerce.adapters.outbound.entities.cart;

import com.application.API_E_commerce.adapters.outbound.entities.user.JpaUserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_carts")
@Data
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

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JpaCartItemEntity> items;
}
