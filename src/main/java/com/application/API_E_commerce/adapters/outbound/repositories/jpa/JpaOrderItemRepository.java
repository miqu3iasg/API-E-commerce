package com.application.API_E_commerce.adapters.outbound.repositories.jpa;

import com.application.API_E_commerce.adapters.outbound.entities.order.JpaOrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaOrderItemRepository extends JpaRepository<JpaOrderItemEntity, UUID> {
}
