package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.CategoryUseCases;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.category.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CategoryServiceImplementation implements CategoryUseCases {
  private final CategoryRepository categoryRepository;

  public CategoryServiceImplementation(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Override
  public Optional<Category> findCategoryById(UUID categoryId) {
    return Optional.ofNullable(this.categoryRepository.findCategoryById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category cannot be null.")));
  }
}
