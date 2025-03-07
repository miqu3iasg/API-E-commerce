package com.application.API_E_commerce.domain.product.dtos;

import com.application.API_E_commerce.domain.category.Category;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UpdateProductRequestDTO(
        Optional<String> name,
        Optional<String> description,
        Optional<BigDecimal> price,
        Optional<Integer> stock,
        Optional<Category> category,
        Optional<List<String>> imagesUrl
) {}
