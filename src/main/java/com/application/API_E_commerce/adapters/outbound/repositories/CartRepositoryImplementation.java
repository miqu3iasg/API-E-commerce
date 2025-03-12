package com.application.API_E_commerce.adapters.outbound.repositories;

import com.application.API_E_commerce.adapters.outbound.entities.cart.JpaCartEntity;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.CartRepository;
import com.application.API_E_commerce.utils.mappers.CartMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class CartRepositoryImplementation implements CartRepository {
  private final JpaCartRepository jpaCartRepository;
  private final CartMapper cartMapper;

  @Autowired
  public CartRepositoryImplementation(JpaCartRepository jpaCartRepository, CartMapper cartMapper) {
    this.jpaCartRepository = jpaCartRepository;
    this.cartMapper = cartMapper;
  }

  @Override
  @Transactional
  public Cart saveCart(Cart cart) {
    JpaCartEntity jpaCartEntityToSave = this.cartMapper.toJpa(cart);

    JpaCartEntity jpaCartEntitySaved = this.jpaCartRepository.save(jpaCartEntityToSave);

    return cartMapper.toDomain(jpaCartEntitySaved);
  }

  @Override
  public Optional<Cart> findCartById(UUID cartId) {
    return Optional.ofNullable(jpaCartRepository.findById(cartId)
            .map(cartMapper::toDomain)
            .orElseThrow(() -> new IllegalArgumentException("Cart not found.")));
  }

  @Override
  public void deleteCartById(UUID cartId) {
    jpaCartRepository.findById(cartId)
            .map(existingCartEntity -> {
              jpaCartRepository.deleteById(cartId);
              return existingCartEntity;
            })
            .orElseThrow(() -> new IllegalArgumentException("Cart not found."));
  }
}
