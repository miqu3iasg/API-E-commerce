package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.category.CategoryRepository;
import com.application.API_E_commerce.domain.category.dtos.CreateCategoryRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
  @InjectMocks
  private CategoryServiceImplementation categoryService;

  @Mock
  private CategoryRepository categoryRepository;

  @Test
  void shouldCreateCategoryWithValidRequestSuccessfully() {
    CreateCategoryRequestDTO createCategoryRequest = new CreateCategoryRequestDTO("Category name", "Category Description");

    Category mockCategory = new Category();
    mockCategory.setId(UUID.randomUUID());
    mockCategory.setName(createCategoryRequest.name());
    mockCategory.setDescription(createCategoryRequest.description());

    when(categoryRepository.saveCategory(any(Category.class))).thenReturn(mockCategory);
    when(categoryRepository.findCategoryById(mockCategory.getId())).thenReturn(Optional.of(mockCategory));

    Category category = categoryService.createCategory(createCategoryRequest);

    log.info("Category Id {}, category name {} and category description {}.",
            category.getId(),
            category.getName(),
            category.getDescription()
    );

    assertNotNull(category.getId());
    assertNotNull(category.getName());
    assertNotNull(category.getDescription());

    Category savedCategory = categoryRepository.findCategoryById(category.getId()).orElse(null);

    assertNotNull(savedCategory);
    assertEquals(savedCategory.getId(), category.getId());
    assertEquals(savedCategory.getName(), category.getName());
    assertEquals(savedCategory.getDescription(), category.getDescription());
  }
}
