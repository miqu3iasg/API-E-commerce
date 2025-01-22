package com.application.API_E_commerce.adapters.outbound.repositories;

import com.application.API_E_commerce.adapters.outbound.entities.product.JpaProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaProductRepository extends JpaRepository<JpaProductEntity, UUID> {

  @Query("SELECT p FROM JpaProductEntity p " +
          "WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
          "AND (:description IS NULL OR LOWER(p.description) LIKE LOWER(CONCAT('%', :description, '%'))) " +
          "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
          "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
          "AND (:categoryId IS NULL OR p.category.id = :categoryId)")
  List<JpaProductEntity> filterProducts(
          @Param("name") String name,
          @Param("description") String description,
          @Param("minPrice") BigDecimal minPrice,
          @Param("maxPrice") BigDecimal maxPrice,
          @Param("categoryId") UUID categoryId
  );
}
