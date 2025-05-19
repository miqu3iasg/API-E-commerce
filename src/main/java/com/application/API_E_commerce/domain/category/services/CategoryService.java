package com.application.API_E_commerce.domain.category.services;

import com.application.API_E_commerce.adapters.inbound.dtos.CreateCategoryRequestDTO;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.category.repository.CategoryRepositoryPort;
import com.application.API_E_commerce.domain.category.useCase.CategoryUseCase;
import com.application.API_E_commerce.infrastructure.exceptions.category.CategoryNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.category.MissingCategoryDescriptionException;
import com.application.API_E_commerce.infrastructure.exceptions.category.MissingCategoryNameException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CategoryService implements CategoryUseCase {

	private final CategoryRepositoryPort categoryRepositoryPort;

	public CategoryService (CategoryRepositoryPort categoryRepositoryPort) {
		this.categoryRepositoryPort = categoryRepositoryPort;
	}

	@Override
	@Transactional
	public Category createCategory (CreateCategoryRequestDTO createCategoryRequest) {
		validateCreateCategoryRequest(createCategoryRequest);

		Category category = new Category(createCategoryRequest.name(), createCategoryRequest.description());

		return categoryRepositoryPort.saveCategory(category);
	}

	private void validateCreateCategoryRequest (CreateCategoryRequestDTO createCategoryRequestDTO) {
		if (createCategoryRequestDTO.name().isEmpty())
			throw new MissingCategoryNameException("The category name cannot be " +
					"empty.");
		if (createCategoryRequestDTO.description().isEmpty())
			throw new MissingCategoryDescriptionException("The category description" +
					" cannot " + "be " + "empty.");
	}

	@Override
	public List<Category> findAllCategories () {
		return categoryRepositoryPort.findAllCategories();
	}

	@Override
	public void deleteCategoryById (UUID categoryId) {
		categoryRepositoryPort.findCategoryById(categoryId).map(existingCategory -> {
			categoryRepositoryPort.deleteCategoryById(categoryId);
			return existingCategory;
		}).orElseThrow(() -> new CategoryNotFoundException("Category cannot be null."));
	}

	@Override
	@Transactional
	public Optional<Category> findCategoryById (UUID categoryId) {
		return Optional.ofNullable(categoryRepositoryPort.findCategoryById(categoryId)
				.orElseThrow(() -> new CategoryNotFoundException("Category cannot be null.")));
	}

}
