package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.ProductUseCases;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.category.CategoryRepository;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.dtos.CreateProductRequestDTO;
import com.application.API_E_commerce.domain.product.dtos.ProductFiltersCriteria;
import com.application.API_E_commerce.domain.product.dtos.UpdateProductRequestDTO;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import com.application.API_E_commerce.utils.validators.CategoryValidator;
import com.application.API_E_commerce.utils.validators.ProductValidator;
import com.cloudinary.utils.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(rollbackOn = Exception.class)
public class ProductServiceImplementation implements ProductUseCases {
  private final ProductRepository productRepository;
  private final ProductValidator productValidator;
  private final CategoryValidator categoryValidator;
  private final CategoryRepository categoryRepository;
  private final CloudinaryServiceImplementation cloudinaryServiceImplementation;

  @PersistenceContext
  private EntityManager entityManager;

  public ProductServiceImplementation(
          ProductRepository productRepository,
          ProductValidator productValidator,
          CategoryValidator categoryValidator, CategoryRepository categoryRepository,
          CloudinaryServiceImplementation cloudinaryServiceImplementation
  ) {
    this.productRepository = productRepository;
    this.productValidator = productValidator;
    this.categoryValidator = categoryValidator;
    this.categoryRepository = categoryRepository;
    this.cloudinaryServiceImplementation = cloudinaryServiceImplementation;
  }

  private Product fetchExistingProduct(UUID productId) {
    return this.productValidator.validateIfProductExistsAndReturnTheExistingProduct(productId);
  }

  private Category fetchExistingCategory(UUID categoryId) {
    return this.categoryValidator.validateIfCategoryExistsAndReturnTheExistingCategory(categoryId);
  }

  @Override
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
                      updateProductRequest.name().ifPresent(existingProduct::setName);
                      updateProductRequest.description().ifPresent(existingProduct::setDescription);
                      updateProductRequest.price().ifPresent(existingProduct::setPrice);
                      updateProductRequest.stock().ifPresent(existingProduct::setStock);
                      updateProductRequest.category().ifPresent(existingProduct::setCategory);

                      updateProductRequest.imagesUrl().ifPresent(newImages -> {
                        List<String> currentImages = existingProduct.getImagesUrl() != null
                                ? new ArrayList<>(existingProduct.getImagesUrl())
                                : new ArrayList<>();

                        currentImages.addAll(newImages);

                        existingProduct.setImagesUrl(currentImages);
                      });
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
  public Product associateProductToCategory(UUID productId, UUID categoryId) {
    Category category = this.categoryRepository.findCategoryById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category was not found."));

    Product product = this.productRepository.findProductById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product was not found."));

    if (product.getCategory() != null) {
      throw new IllegalArgumentException("Category already exists in product.");
    }

    category.getProducts().add(product);

    product.setCategory(category);

    return this.productRepository.saveProduct(product);
  }

  @Override
  public Category getProductCategory(UUID productId) {
    Product product = this.productValidator.validateIfProductExistsAndReturnTheExistingProduct(productId);

    return product.getCategory();
  }

  @Override
  public Product removeProductFromCategory(UUID productId) {
    return this.productRepository.findProductById(productId)
            .map(existingProduct -> {
              if (existingProduct.getCategory() == null) {
                throw new IllegalArgumentException("Product category cannot be null.");
              }

              existingProduct.setCategory(null);

              return productRepository.saveProduct(existingProduct);
            }).orElseThrow(() -> new IllegalArgumentException("Product not found."));
  }

  @Override
  public void uploadProductImage(UUID productId, List<String> imagesUrl) throws IOException {
    Product product = this.productValidator.validateIfProductExistsAndReturnTheExistingProduct(productId);

    List<String> existingImages = product.getImagesUrl() != null
            ? new ArrayList<>(product.getImagesUrl())
            : new ArrayList<>();

    imagesUrl.forEach(imageUrl -> {
      try {
        this.uploadImageToCloudinary(imageUrl, productId);
        existingImages.add(imageUrl);
      } catch (IOException e) {
        throw new RuntimeException("Error when upload image: " + imageUrl, e);
      }
    });

    product.setImagesUrl(existingImages);

    this.productRepository.saveProduct(product);
  }

  private void uploadImageToCloudinary(String imageUrl, UUID productId) throws IOException {
    try {
      this.cloudinaryServiceImplementation.uploadToImageCloudinary(imageUrl, productId);
      log.info("Image successfully uploaded to Cloudinary with url: {}", imageUrl);
    } catch (IOException exception) {
      log.error("Error uploading image to Cloudinary: {}", exception.getMessage());
      throw exception;
    }
  }

  @Override
  public List<String> getProductImages(UUID productId) {
    Product product = this.productValidator.validateIfProductExistsAndReturnTheExistingProduct(productId);
    return product.getImagesUrl();
  }

  @Override
  public void deleteProductImage(UUID productId) throws IOException {
    Product product = this.productValidator.validateIfProductExistsAndReturnTheExistingProduct(productId);

    if (product.getImagesUrl().isEmpty() || product.getImagesUrl() == null) {
      throw new IllegalArgumentException("Product image was not found.");
    }

    this.cloudinaryServiceImplementation.deleteImageFromCloudinary(productId);

    product.setImagesUrl(null);

    productRepository.saveProduct(product);

    log.info("Image removed from product {} and deleted from Cloudinary.", productId);
  }

  @Override
  public void updateStockAfterSale(UUID productId, int quantitySold) {
    Product product = this.productValidator.validateIfProductExistsAndReturnTheExistingProduct(productId);

    int actualStock = product.getStock();

    int stockAfterSale = actualStock - quantitySold;

    product.setStock(stockAfterSale);

    this.productRepository.saveProduct(product);
  }

  @Override
  public int getProductStock(UUID productId) {
    Product product = this.productValidator.validateIfProductExistsAndReturnTheExistingProduct(productId);

    return product.getStock();
  }

  @Override
  public List<Product> filterProducts(ProductFiltersCriteria criteria) {
    if (criteria == null) throw new IllegalArgumentException("The parameters can't be null.");
    return this.productRepository.filterProducts(criteria);
  }
}
