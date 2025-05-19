package com.application.API_E_commerce.adapters.outbound.repositories.jpa;

import com.application.API_E_commerce.adapters.outbound.entities.cart.JpaCartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaCartItemRepository extends JpaRepository<JpaCartItemEntity, UUID> {
}
