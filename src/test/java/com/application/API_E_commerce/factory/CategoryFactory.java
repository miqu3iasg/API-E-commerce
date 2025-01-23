package com.application.API_E_commerce.factory;

import com.application.API_E_commerce.domain.category.Category;

import java.util.UUID;


public class CategoryFactory {
  public static Category build() {
    Category category = new Category();
    category.setId(UUID.randomUUID());
    category.setName("Test Category");
    category.setDescription("Test description");

    return category;
  }
}
