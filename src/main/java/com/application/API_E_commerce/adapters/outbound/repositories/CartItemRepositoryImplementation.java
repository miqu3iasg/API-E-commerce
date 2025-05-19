package com.application.API_E_commerce.adapters.outbound.repositories;

import com.application.API_E_commerce.adapters.outbound.entities.cart.JpaCartItemEntity;
import com.application.API_E_commerce.adapters.outbound.repositories.jpa.JpaCartItemRepository;
import com.application.API_E_commerce.common.utils.mappers.CartItemMapper;
import com.application.API_E_commerce.domain.cart.cartitem.CartItem;
import com.application.API_E_commerce.domain.cart.cartitem.repository.CartItemRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class CartItemRepositoryImplementation implements CartItemRepositoryPort {

	private final JpaCartItemRepository jpaCartItemRepository;
	private final CartItemMapper cartItemMapper;

	public CartItemRepositoryImplementation (JpaCartItemRepository jpaCartItemRepository, CartItemMapper cartItemMapper) {
		this.jpaCartItemRepository = jpaCartItemRepository;
		this.cartItemMapper = cartItemMapper;
	}

	@Override
	public CartItem saveCartItem (CartItem cartItem) {
		JpaCartItemEntity jpaCartItemToSave = cartItemMapper.toJpa(cartItem);

		JpaCartItemEntity jpaCartItemSaved = jpaCartItemRepository.save(jpaCartItemToSave);

		return cartItemMapper.toDomain(jpaCartItemSaved);
	}

	@Override
	public void deleteCartItem (CartItem cartItem) {
		if (cartItem.getId() == null)
			throw new IllegalArgumentException("Cart item id cannot be null.");

		jpaCartItemRepository.findById(cartItem.getId())
				.map(existingJpaCartItem -> {
					jpaCartItemRepository.delete(existingJpaCartItem);
					return existingJpaCartItem;
				}).orElseThrow(() -> new IllegalArgumentException("Cart item was not found."));
	}

	@Override
	public Optional<CartItem> findCartItemById (UUID cartItemId) {
		if (cartItemId == null)
			throw new IllegalArgumentException("Cart item id cannot be null.");

		return Optional.ofNullable(jpaCartItemRepository.findById(cartItemId)
				.map(cartItemMapper::toDomain)
				.orElseThrow(() -> new IllegalArgumentException("Cart item was not found.")));
	}

	@Override
	public void deleteCartItemById (UUID cartItemId) {
		if (cartItemId == null)
			throw new IllegalArgumentException("Cart item id cannot be null.");

		jpaCartItemRepository.findById(cartItemId)
				.map(existingJpaCartItem -> {
					jpaCartItemRepository.delete(existingJpaCartItem);
					return existingJpaCartItem;
				}).orElseThrow(() -> new IllegalArgumentException("Cart item was not found."));
	}

}
