package com.application.API_E_commerce.adapters.outbound.repositories.jpa;

import com.application.API_E_commerce.adapters.outbound.entities.category.JpaCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCategoryRepository extends JpaRepository<JpaCategoryEntity, UUID> {

	@Query("SELECT c FROM JpaCategoryEntity c LEFT JOIN FETCH c.products WHERE c.id = :id")
	Optional<JpaCategoryEntity> findCategoryByIdWithProducts (@Param("id") UUID categoryId);

}
