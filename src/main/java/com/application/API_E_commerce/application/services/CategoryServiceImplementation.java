package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.CategoryUseCases;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.category.CategoryRepository;
import com.application.API_E_commerce.domain.category.dtos.CreateCategoryRequestDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CategoryServiceImplementation implements CategoryUseCases {
  private final CategoryRepository categoryRepository;

  public CategoryServiceImplementation(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Override
  @Transactional
  public Category createCategory(CreateCategoryRequestDTO createCategoryRequest) {
    validateCreateCategoryRequest(createCategoryRequest);

    Category category = new Category(createCategoryRequest.name(), createCategoryRequest.description());

    return this.categoryRepository.saveCategory(category);
  }

  private void validateCreateCategoryRequest(CreateCategoryRequestDTO createCategoryRequestDTO) {
    if (createCategoryRequestDTO.name().isEmpty()) throw new IllegalArgumentException("The category name cannot be empty.");
    if (createCategoryRequestDTO.description().isEmpty()) throw new IllegalArgumentException("The category description cannot be empty.");
  }

  @Override
  public List<Category> findAllCategories() {
    return List.of();
  }

  @Override
  public void deleteCategoryById(UUID categoryId) {

  }

  @Override
  @Transactional
  public Optional<Category> findCategoryById(UUID categoryId) {
    return Optional.ofNullable(this.categoryRepository.findCategoryById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category cannot be null.")));
  }
}
