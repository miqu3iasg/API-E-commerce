package com.application.API_E_commerce.domain.category.useCase;

import com.application.API_E_commerce.adapters.inbound.dtos.CreateCategoryRequestDTO;
import com.application.API_E_commerce.domain.category.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryUseCase {

	Category createCategory (CreateCategoryRequestDTO createCategoryRequest);

	List<Category> findAllCategories ();

	void deleteCategoryById (UUID categoryId);

	Optional<Category> findCategoryById (UUID categoryId);

}
