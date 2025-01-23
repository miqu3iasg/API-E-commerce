package com.application.API_E_commerce.domain.product.dtos;

import com.application.API_E_commerce.domain.category.Category;

import java.math.BigDecimal;

public record UpdateProductRequestDTO(
        String name,
        String description,
        BigDecimal price,
        int stock,
        Category category,
        String imageUrl
) {
}
