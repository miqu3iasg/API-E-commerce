package com.application.API_E_commerce.adapters.outbound.repositories;

import com.application.API_E_commerce.adapters.outbound.entities.category.JpaCategoryEntity;
import com.application.API_E_commerce.adapters.outbound.entities.product.JpaProductEntity;
import com.application.API_E_commerce.adapters.outbound.repositories.jpa.JpaCategoryRepository;
import com.application.API_E_commerce.common.utils.mappers.CategoryMapper;
import com.application.API_E_commerce.common.utils.mappers.ProductMapper;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.category.repository.CategoryRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CategoryRepositoryImplementation implements CategoryRepositoryPort {

	private final JpaCategoryRepository jpaCategoryRepository;
	private final ProductMapper productMapper;
	private final CategoryMapper categoryMapper;

	public CategoryRepositoryImplementation (JpaCategoryRepository jpaCategoryRepository, ProductMapper productMapper, CategoryMapper categoryMapper) {
		this.jpaCategoryRepository = jpaCategoryRepository;
		this.productMapper = productMapper;
		this.categoryMapper = categoryMapper;
	}

	@Override
	@Transactional
	public Category saveCategory (Category category) {
		if (category.getId() != null) {
			JpaCategoryEntity existingEntity = jpaCategoryRepository.findById(category.getId()).orElse(null);

			if (existingEntity != null) {
				JpaCategoryEntity categoryEntityToSave = updateExistingEntity(existingEntity, category);
				JpaCategoryEntity categoryEntityToConvert = jpaCategoryRepository.save(categoryEntityToSave);
				return categoryMapper.toDomain(categoryEntityToConvert);
			}
		}

		JpaCategoryEntity jpaCategoryToSave = categoryMapper.toJpa(category);

		JpaCategoryEntity jpaCategoryEntityToConvert = jpaCategoryRepository.save(jpaCategoryToSave);

		return categoryMapper.toDomain(jpaCategoryEntityToConvert);
	}

	private JpaCategoryEntity updateExistingEntity (JpaCategoryEntity existingEntity, Category category) {
		existingEntity.setName(category.getName());
		existingEntity.setDescription(category.getDescription());

		if (category.getProducts() != null) {
			List<JpaProductEntity> productEntities = category.getProducts()
					.stream()
					.map(productMapper::toJpa)
					.collect(Collectors.toList());

			existingEntity.setProducts(productEntities);
		} else existingEntity.setProducts(null);

		existingEntity.setVersion(category.getVersion());

		return existingEntity;
	}

	@Override
	@Transactional
	public Optional<Category> findCategoryById (UUID categoryId) {
		return Optional.ofNullable(jpaCategoryRepository.findById(categoryId)
				.map(categoryMapper::toDomain)
				.orElseThrow(() -> new IllegalArgumentException("Category was not found when searching for id in the repository.")));
	}

	@Override
	public List<Category> findAllCategories () {
		if (jpaCategoryRepository.findAll().isEmpty())
			return Collections.emptyList();

		return jpaCategoryRepository.findAll().stream().map(categoryMapper::toDomain).toList();
	}

	@Override
	public void deleteCategoryById (UUID categoryId) {
		jpaCategoryRepository.findById(categoryId).map(existingCategory -> {
			jpaCategoryRepository.deleteById(categoryId);
			return existingCategory;
		}).orElseThrow(() -> new IllegalArgumentException("Category cannot be null."));
	}

}
