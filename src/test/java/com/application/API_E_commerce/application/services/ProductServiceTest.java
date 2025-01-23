package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.ProductUseCases;
import com.application.API_E_commerce.domain.category.CategoryRepository;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.dtos.CreateProductRequestDTO;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
class ProductServiceTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ProductUseCases productService;

  @Nested
  class CreateProduct {
    @Test
    @DisplayName("Should create a product successfully with valid inputs")
    void shouldCreateProductSuccessfully() {
      CreateProductRequestDTO createProductRequest = new CreateProductRequestDTO(
              "Test Name",
              "Test description",
              BigDecimal.valueOf(20.0),
              10
      );

      Product product = productService.createProduct(createProductRequest);

      assertNotNull(product, "The product should not be null");
      assertNotNull(product.getId(), "The product ID should not be null");

      assertEquals("Test Name", product.getName(), "The product name is incorrect");
      assertEquals("Test description", product.getDescription(), "The product description is incorrect");
      assertEquals(BigDecimal.valueOf(20.0), product.getPrice(), "The product price is incorrect");
      assertEquals(10, product.getStock(), "The product stock is incorrect");

      Product savedProduct = productRepository.findProductById(product.getId()).orElse(null);
      assertNotNull(savedProduct, "The product should be saved in the database");
      assertEquals(product.getId(), savedProduct.getId(), "The product ID should match");
      assertEquals(product.getName(), savedProduct.getName(), "The product name should match");
      assertEquals(product.getDescription(), savedProduct.getDescription(), "The product description should match");
      assertEquals(0, product.getPrice().compareTo(BigDecimal.valueOf(20.0)), "The product price should match");
      assertEquals(product.getStock(), savedProduct.getStock(), "The product stock should match");
    }
  }

  @Nested
  class ListProduct {
    @Test
    @DisplayName("Should return a list of product images when the product exists")
    void shouldReturnListOfProductImagesWhenProductExists() {}
  }

  @Nested
  class UpdateProduct {

    @Test
    @DisplayName("Should update the product details when the product exists")
    void shouldUpdateProductDetailsWhenProductExists() {}

    @Test
    @DisplayName("Should update the product stock after a sale when the product exists")
    void shouldUpdateProductStockAfterSaleWhenProductExists() {}
  }

  @Nested
  class ProductCategory {

    @Test
    @DisplayName("Should associate a product to a category when both product and category exist")
    void shouldAssociateProductToCategoryWhenBothProductAndCategoryExist() {}

    @Test
    @DisplayName("Should return the product category when the product exists")
    void shouldReturnProductCategoryWhenProductExists() {}

    @Test
    @DisplayName("Should remove the product from the category when both product and category exist")
    void shouldRemoveProductFromCategoryWhenProductAndCategoryExist() {}
  }
}