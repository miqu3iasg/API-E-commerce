package com.application.API_E_commerce.adapters.outbound.repositories;

import com.application.API_E_commerce.adapters.outbound.entities.category.JpaCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaCategoryRepository extends JpaRepository<JpaCategoryEntity, UUID> {
}
