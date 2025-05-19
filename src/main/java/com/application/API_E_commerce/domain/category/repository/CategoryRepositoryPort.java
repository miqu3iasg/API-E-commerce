package com.application.API_E_commerce.domain.category.repository;

import com.application.API_E_commerce.domain.category.Category;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepositoryPort {

	Category saveCategory (Category category);

	Optional<Category> findCategoryById (UUID categoryId);

	List<Category> findAllCategories ();

	void deleteCategoryById (UUID categoryId);

}
