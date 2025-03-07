package com.application.API_E_commerce.domain.category;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository {
  Category saveCategory(Category category);
  Optional<Category> findCategoryById(UUID categoryId);
  List<Category> findAllCategories();
  void deleteCategoryById(UUID categoryId);
}
