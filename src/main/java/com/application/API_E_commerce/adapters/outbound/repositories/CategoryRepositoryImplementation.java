package com.application.API_E_commerce.adapters.outbound.repositories;

import com.application.API_E_commerce.adapters.outbound.entities.category.JpaCategoryEntity;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.category.CategoryRepository;
import com.application.API_E_commerce.utils.converters.CategoryConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CategoryRepositoryImplementation implements CategoryRepository {
  private final JpaCategoryRepository jpaCategoryRepository;
  private final CategoryConverter categoryConverter;

  public CategoryRepositoryImplementation(JpaCategoryRepository jpaCategoryRepository, CategoryConverter categoryConverter) {
    this.jpaCategoryRepository = jpaCategoryRepository;
    this.categoryConverter = categoryConverter;
  }

  @Override
  public Category saveCategory(Category category) {
    JpaCategoryEntity jpaCategoryToSave = this.categoryConverter.toJpa(category);
    JpaCategoryEntity jpaCategoryEntityToConvert = this.jpaCategoryRepository.save(jpaCategoryToSave);

    return categoryConverter.toDomain(jpaCategoryEntityToConvert);
  }

  @Override
  public Optional<Category> findCategoryById(UUID categoryId) {
    return Optional.ofNullable(this.jpaCategoryRepository.findById(categoryId)
            .map(categoryConverter::toDomain)
            .orElseThrow(() -> new IllegalArgumentException("Category was not found when searching for id in the repository.")));
  }

  @Override
  public List<Category> findAllCategories() {
    return List.of();
  }

  @Override
  public void deleteCategoryById(UUID categoryId) {

  }
}
