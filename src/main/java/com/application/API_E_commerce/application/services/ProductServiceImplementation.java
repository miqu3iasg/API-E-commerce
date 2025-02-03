package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.ProductUseCases;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.dtos.CreateProductRequestDTO;
import com.application.API_E_commerce.domain.product.dtos.ProductFiltersCriteria;
import com.application.API_E_commerce.domain.product.dtos.UpdateProductRequestDTO;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import com.application.API_E_commerce.utils.validators.CategoryValidator;
import com.application.API_E_commerce.utils.validators.ProductValidator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ProductServiceImplementation implements ProductUseCases {
  private final ProductRepository productRepository;
  private final ProductValidator productValidator;
  private final CategoryValidator categoryValidator;
  private final CloudinaryServiceImplementation cloudinaryServiceImplementation;

  public ProductServiceImplementation(
          ProductRepository productRepository,
          ProductValidator productValidator,
          CategoryValidator categoryValidator, CloudinaryServiceImplementation cloudinaryServiceImplementation
  ) {
    this.productRepository = productRepository;
    this.productValidator = productValidator;
    this.categoryValidator = categoryValidator;
    this.cloudinaryServiceImplementation = cloudinaryServiceImplementation;
  }

  private Product fetchExistingProduct(UUID productId) {
    return this.productValidator.validateIfProductExistsAndReturnTheExistingProduct(productId);
  }

  private Category fetchExistingCategory(UUID categoryId) {
    return this.categoryValidator.validateIfCategoryExistsAndReturnTheExistingCategory(categoryId);
  }

  @Override
  @Transactional
  public Product createProduct(CreateProductRequestDTO createProductRequest) {
    this.productValidator.validateCreateProductRequest(createProductRequest);

    Product product = new Product();
    product.setName(createProductRequest.name());
    product.setDescription(createProductRequest.description());
    product.setPrice(createProductRequest.price());
    product.setStock(createProductRequest.stock());
    product.setCreatedAt(LocalDateTime.now());

    return this.productRepository.saveProduct(product);
  }

  @Override
  public Optional<Product> findProductById(UUID productId) {
    return this.productRepository.findProductById(productId);
  }

  @Override
  public List<Product> findAllProducts() {
    return this.productRepository.findAllProducts();
  }

  @Override
  public void updateProduct(UUID productId, UpdateProductRequestDTO updateProductRequest) {
    this.productRepository.findProductById(productId)
            .ifPresentOrElse(
                    existingProduct -> {
                      existingProduct.setName(updateProductRequest.name());
                      existingProduct.setDescription(updateProductRequest.description());
                      existingProduct.setPrice(updateProductRequest.price());
                      existingProduct.setStock(updateProductRequest.stock());
                      existingProduct.setCategory(updateProductRequest.category());
                      existingProduct.setImageUrl(updateProductRequest.imageUrl());
                      this.productRepository.saveProduct(existingProduct);
                    },
                    () -> { throw new IllegalArgumentException("Product was not found."); }
            );
  }

  @Override
  public void deleteProduct(UUID productId) {
    this.productRepository.findProductById(productId)
            .ifPresentOrElse(
                    existingProduct -> this.productRepository.deleteProductById(productId),
                    () -> { throw new IllegalArgumentException("Product was not found."); }
            );
  }

  @Override
  @Transactional
  public void associateProductToCategory(UUID productId, UUID categoryId) {
    Category category = this.fetchExistingCategory(categoryId);

    Product product = this.fetchExistingProduct(productId);

    product.setCategory(category);

    this.productRepository.saveProduct(product);
  }

  @Override
  public Category getProductCategory(UUID productId) {
    Product product = this.fetchExistingProduct(productId);

    return product.getCategory();
  }

  @Override
  public void removeProductFromCategory(UUID productId) {
    Product product = this.fetchExistingProduct(productId);

    UUID categoryId = product.getCategory().getId();
    this.fetchExistingCategory(categoryId);

    product.setCategory(null);

    this.productRepository.saveProduct(product);
  }

  @Override
  @Transactional
  public void uploadProductImage(UUID productId, String imageUrl, String imageName) throws IOException {

    this.uploadImageToCloudinary(imageUrl, imageName);

    Product product = this.fetchExistingProduct(productId);

    product.setImageUrl(imageUrl);

    this.productRepository.saveProduct(product);
  }

  @Transactional
  private void uploadImageToCloudinary(String imageUrl, String imageName) throws IOException {
    try {
      this.cloudinaryServiceImplementation.uploadToImageCloudinary(imageUrl, imageName);
      log.info("Image successfully uploaded to Cloudinary with name: {}", imageName);
    } catch (IOException exception) {
      log.error("Error uploading image to Cloudinary: {}", exception.getMessage());
      throw exception;
    }
  }

  @Override
  public List<String> getProductImages(UUID productId) {
    Product product = this.fetchExistingProduct(productId);
    return List.of(product.getImageUrl());
  }

  @Override
  public void deleteProductImage(UUID productId, String imageUrl) {
    // Definir a melhor abordagem para a implementação desse método
  }

  @Override
  public void updateStockAfterSale(UUID productId, int quantitySold) {
    Product product = this.fetchExistingProduct(productId);

    int actualStock = product.getStock();

    int stockAfterSale = actualStock - quantitySold;

    product.setStock(stockAfterSale);

    this.productRepository.saveProduct(product);
  }

  @Override
  public int getProductStock(UUID productId) {
    Product product = this.fetchExistingProduct(productId);

    return product.getStock();
  }

  @Override
  public List<Product> filterProducts(ProductFiltersCriteria criteria) {
    if (criteria == null) throw new IllegalArgumentException("The parameters can't be null.");
    return this.productRepository.filterProducts(criteria);
  }
}
