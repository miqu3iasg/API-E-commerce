package com.application.API_E_commerce.application.usecases;

import com.application.API_E_commerce.domain.category.Category;

import java.util.Optional;
import java.util.UUID;

public interface CategoryUseCases {
  Optional<Category> findCategoryById(UUID categoryId);
}
