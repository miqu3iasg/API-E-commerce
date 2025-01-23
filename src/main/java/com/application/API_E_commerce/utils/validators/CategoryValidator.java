package com.application.API_E_commerce.utils.validators;

import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.category.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CategoryValidator {
  private final CategoryRepository categoryRepository;

  public CategoryValidator(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public Category validateIfCategoryExistsAndReturnTheExistingCategory(UUID categoryId) {
    Optional<Category> categoryOptional = this.categoryRepository.findCategoryById(categoryId);

    if (categoryOptional.isEmpty()) {
      throw new IllegalArgumentException("Category was not found.");
    }

    return categoryOptional.get();
  }
}
