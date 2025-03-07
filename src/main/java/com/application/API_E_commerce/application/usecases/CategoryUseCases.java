package com.application.API_E_commerce.application.usecases;

import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.category.dtos.CreateCategoryRequestDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryUseCases {
  Category createCategory(CreateCategoryRequestDTO createCategoryRequest);
  List<Category> findAllCategories();
  void deleteCategoryById(UUID categoryId);
  Optional<Category> findCategoryById(UUID categoryId);
}
