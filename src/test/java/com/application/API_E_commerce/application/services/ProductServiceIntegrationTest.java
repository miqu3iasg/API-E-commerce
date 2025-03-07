package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.CategoryUseCases;
import com.application.API_E_commerce.application.usecases.ProductUseCases;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.category.CategoryRepository;
import com.application.API_E_commerce.domain.category.dtos.CreateCategoryRequestDTO;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.dtos.CreateProductRequestDTO;
import com.application.API_E_commerce.domain.product.dtos.UpdateProductRequestDTO;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
class ProductServiceIntegrationTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ProductUseCases productService;

  @Autowired
  private CategoryUseCases categoryService;

  @Nested
  class CreateProduct {
    @Test
    @DisplayName("Should create a product successfully with valid inputs")
    void shouldCreateProductSuccessfully() {
      CreateProductRequestDTO createProductRequest = createProductRequestFactory();

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
    void shouldReturnListOfProductImagesWhenProductExists() {
      CreateProductRequestDTO createProductRequest = createProductRequestFactory();
      Product product = productService.createProduct(createProductRequest);

      product.setImagesUrl(List.of("image1.jpg", "image2.jpg"));

      productRepository.saveProduct(product);

      List<String> images = productService.getProductImages(product.getId());

      assertNotNull(images, "The list of images should not be null");
      assertTrue(images.contains("image1.jpg"), "The product should contain image1.jpg");
      assertTrue(images.contains("image2.jpg"), "The product should contain image2.jpg");
    }
  }

  @Nested
  class UpdateProduct {

    @Test
    @DisplayName("Should update the product details when the product exists")
    void shouldUpdateProductDetailsWhenProductExists() {
      CreateProductRequestDTO createProductRequest = createProductRequestFactory();
      Product product = productService.createProduct(createProductRequest);

      String updatedName = "Updated Name";
      String updatedDescription = "Updated Description";
      BigDecimal updatedPrice = BigDecimal.valueOf(50.0);

      UpdateProductRequestDTO updateProductRequest = new UpdateProductRequestDTO(
              Optional.of(updatedName),
              Optional.of(updatedDescription),
              Optional.of(updatedPrice),
              Optional.empty(),
              Optional.empty(),
              Optional.empty()
      );

      productService.updateProduct(product.getId(), updateProductRequest);

      Product updatedProduct = productRepository.findProductById(product.getId()).orElse(null);
      assertNotNull(updatedProduct, "The updated product should not be null");
      assertEquals("Updated Name", updatedProduct.getName(), "The product name should be updated");
      assertEquals("Updated Description", updatedProduct.getDescription(), "The product description should be updated");
      assertEquals(0, updatedProduct.getPrice().compareTo(BigDecimal.valueOf(50.0)), "The product price should be updated");
    }

    @Test
    @DisplayName("Should update the product stock after a sale when the product exists")
    void shouldUpdateProductStockAfterSaleWhenProductExists() {
      CreateProductRequestDTO createProductRequest = createProductRequestFactory();
      Product product = productService.createProduct(createProductRequest);

      int quantitySold = 3;

      productService.updateStockAfterSale(product.getId(), quantitySold);


      Product updatedProduct = productRepository.findProductById(product.getId()).orElse(null);
      assertNotNull(updatedProduct, "The updated product should not be null");
      assertEquals(7, updatedProduct.getStock(), "The product stock should be updated correctly");
    }
  }

  @Nested
  class ProductCategory {

    @Test
    @Transactional
    @DisplayName("Should associate a product to a category when both product and category exist")
    void shouldAssociateProductToCategoryWhenBothProductAndCategoryExist() {
      CreateProductRequestDTO createProductRequest = createProductRequestFactory();
      Product product = productService.createProduct(createProductRequest);

      CreateCategoryRequestDTO createCategoryRequest = createCategoryRequestFactory();
      Category category = categoryService.createCategory(createCategoryRequest);

      log.info("Product {} and category {} created successfully.", product.getId(), category.getId());

      Product createdProduct = productRepository.findProductById(product.getId()).orElse(null);
      assertNotNull(createdProduct, "The created product should not be null");

      Category createdCategory = categoryRepository.findCategoryById(category.getId()).orElse(null);
      assertNotNull(createdCategory, "The created category should not be null");

      productService.associateProductToCategory(createdProduct.getId(), createdCategory.getId());

      Product savedProduct = productRepository.findProductById(product.getId()).orElse(null);
      assertNotNull(savedProduct, "The product should be saved in the database");
      assertEquals(category.getId(), savedProduct.getCategory().getId(), "The product category should match");
    }

    @Test
    @DisplayName("Should return the product category when the product exists")
    void shouldReturnProductCategoryWhenProductExists() {
      CreateProductRequestDTO createProductRequest = createProductRequestFactory();
      Product product = productService.createProduct(createProductRequest);

      CreateCategoryRequestDTO createCategoryRequest = createCategoryRequestFactory();
      Category category = categoryService.createCategory(createCategoryRequest);

      productService.associateProductToCategory(product.getId(), category.getId());

      Category retrievedCategory = productService.getProductCategory(product.getId());

      assertNotNull(retrievedCategory, "The product category should not be null");
      assertEquals(category.getId(), retrievedCategory.getId(), "The product category ID should match");
      assertEquals(category.getName(), retrievedCategory.getName(), "The product category name should match");
    }

    @Test
    @Transactional
    @DisplayName("Should remove the product from the category when both product and category exist")
    void shouldRemoveProductFromCategoryWhenProductAndCategoryExist() {
      CreateProductRequestDTO createProductRequest = createProductRequestFactory();
      Product product = productService.createProduct(createProductRequest);

      CreateCategoryRequestDTO createCategoryRequest = createCategoryRequestFactory();
      Category category = categoryService.createCategory(createCategoryRequest);

      Product productWithCategory = productService.associateProductToCategory(product.getId(), category.getId());

      assertNotNull(productWithCategory.getCategory(), "The product should be associated with a category before removal");
      List<Product> categoryWithProducts = productWithCategory.getCategory().getProducts();

      log.info("Products in category: {}", categoryWithProducts.size());
      log.info("Product being checked: {}", productWithCategory);

      assertTrue(categoryWithProducts.contains(productWithCategory), "The category should contain the associated product");

      log.info("Product id: {}, product category: {}", productWithCategory.getId(), productWithCategory.getCategory().getId());
      log.info("Product in category: {}", categoryWithProducts);

      Product productWithoutCategory = productService.removeProductFromCategory(productWithCategory.getId());

      assertNull(productWithoutCategory.getCategory(), "The product should have been removed from the category");

      Product updatedProduct = productRepository.findProductById(product.getId()).orElse(null);
      assertNotNull(updatedProduct, "The updated product should not be null");
      assertNull(updatedProduct.getCategory(), "The product should no longer have a category");
    }
  }

  private CreateProductRequestDTO createProductRequestFactory() {
    return new CreateProductRequestDTO(
            "Test Name",
            "Test description",
            BigDecimal.valueOf(20.0),
            10
    );
  }

  private CreateCategoryRequestDTO createCategoryRequestFactory() {
    return new CreateCategoryRequestDTO("Category name", "Category description");
  }
}

