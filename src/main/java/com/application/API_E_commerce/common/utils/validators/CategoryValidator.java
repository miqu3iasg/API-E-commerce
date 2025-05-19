package com.application.API_E_commerce.common.utils.validators;

import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.category.repository.CategoryRepositoryPort;
import com.application.API_E_commerce.infrastructure.exceptions.category.CategoryNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CategoryValidator {

	private final CategoryRepositoryPort categoryRepositoryPort;

	public CategoryValidator (CategoryRepositoryPort categoryRepositoryPort) {
		this.categoryRepositoryPort = categoryRepositoryPort;
	}

	public Category validateIfCategoryExistsAndReturnTheExistingCategory (UUID categoryId) {
		Optional<Category> categoryOptional = categoryRepositoryPort.findCategoryById(categoryId);

		if (categoryOptional.isEmpty())
			throw new CategoryNotFoundException("Category was not found.");

		return categoryOptional.get();
	}

}
