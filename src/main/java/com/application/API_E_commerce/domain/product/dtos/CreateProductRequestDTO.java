package com.application.API_E_commerce.domain.product.dtos;

import java.math.BigDecimal;

public record CreateProductRequestDTO(
        String name,
        String description,
        BigDecimal price,
        int stock
) {
}
