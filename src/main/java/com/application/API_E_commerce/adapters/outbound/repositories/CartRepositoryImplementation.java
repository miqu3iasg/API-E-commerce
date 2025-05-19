package com.application.API_E_commerce.adapters.outbound.repositories;

import com.application.API_E_commerce.adapters.outbound.entities.cart.JpaCartEntity;
import com.application.API_E_commerce.adapters.outbound.repositories.jpa.JpaCartRepository;
import com.application.API_E_commerce.common.utils.mappers.CartMapper;
import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.cart.repository.CartRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class CartRepositoryImplementation implements CartRepositoryPort {

	private final JpaCartRepository jpaCartRepository;
	private final CartMapper cartMapper;

	@Autowired
	public CartRepositoryImplementation (JpaCartRepository jpaCartRepository, CartMapper cartMapper) {
		this.jpaCartRepository = jpaCartRepository;
		this.cartMapper = cartMapper;
	}

	@Override
	@Transactional
	public Cart saveCart (Cart cart) {
		JpaCartEntity jpaCartEntityToSave = cartMapper.toJpa(cart);

		JpaCartEntity jpaCartEntitySaved = jpaCartRepository.save(jpaCartEntityToSave);

		return cartMapper.toDomain(jpaCartEntitySaved);
	}

	@Override
	public Optional<Cart> findCartById (UUID cartId) {
		return Optional.ofNullable(jpaCartRepository.findById(cartId)
				.map(cartMapper::toDomain)
				.orElseThrow(() -> new IllegalArgumentException("Cart not found.")));
	}

	@Override
	public void deleteCartById (UUID cartId) {
		jpaCartRepository.findById(cartId)
				.map(existingCartEntity -> {
					jpaCartRepository.deleteById(cartId);
					return existingCartEntity;
				})
				.orElseThrow(() -> new IllegalArgumentException("Cart not found."));
	}

}
