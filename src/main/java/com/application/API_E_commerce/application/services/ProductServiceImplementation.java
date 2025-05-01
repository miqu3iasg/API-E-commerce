package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.ProductUseCases;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.category.CategoryRepository;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.dtos.CreateProductRequestDTO;
import com.application.API_E_commerce.domain.product.dtos.ProductFiltersCriteria;
import com.application.API_E_commerce.domain.product.dtos.UpdateProductRequestDTO;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import com.application.API_E_commerce.infrastructure.exceptions.NullParametersException;
import com.application.API_E_commerce.infrastructure.exceptions.category.CategoryAlreadyExistsException;
import com.application.API_E_commerce.infrastructure.exceptions.category.CategoryNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.product.InvalidQuantityException;
import com.application.API_E_commerce.infrastructure.exceptions.product.ProductImageNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.product.ProductNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.product.ProductOutOfStockException;
import com.application.API_E_commerce.utils.validators.CategoryValidator;
import com.application.API_E_commerce.utils.validators.ProductValidator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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

	public ProductServiceImplementation (
			ProductRepository productRepository,
			ProductValidator productValidator,
			CategoryValidator categoryValidator,
			CategoryRepository categoryRepository,
			CloudinaryServiceImplementation cloudinaryServiceImplementation
	) {
		this.productRepository = productRepository;
		this.productValidator = productValidator;
		this.categoryValidator = categoryValidator;
		this.categoryRepository = categoryRepository;
		this.cloudinaryServiceImplementation = cloudinaryServiceImplementation;
	}

	private Product fetchExistingProduct (UUID productId) {
		return productValidator.validateIfProductExistsAndReturnTheExistingProduct(productId);
	}

	private Category fetchExistingCategory (UUID categoryId) {
		return categoryValidator.validateIfCategoryExistsAndReturnTheExistingCategory(categoryId);
	}

	@Override
	public Product createProduct (CreateProductRequestDTO createProductRequest) {
		productValidator.validateCreateProductRequest(createProductRequest);

		Product product = new Product();
		product.setName(createProductRequest.name());
		product.setDescription(createProductRequest.description());
		product.setPrice(createProductRequest.price());
		product.setStock(createProductRequest.stock());
		product.setCreatedAt(LocalDateTime.now());

		return productRepository.saveProduct(product);
	}

	@Override
	public Optional<Product> findProductById (UUID productId) {
		return productRepository.findProductById(productId);
	}

	@Override
	public List<Product> findAllProducts () {
		return productRepository.findAllProducts();
	}

	@Override
	public Product updateProduct (UUID productId, UpdateProductRequestDTO updateProductRequest) {
		return productRepository.findProductById(productId)
				.map(existingProduct -> {
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
					return productRepository.saveProduct(existingProduct);
				})
				.orElseThrow(() -> new ProductNotFoundException());
	}

	@Override
	public void deleteProduct (UUID productId) {
		productRepository.findProductById(productId)
				.ifPresentOrElse(
						existingProduct -> productRepository.deleteProductById(productId),
						() -> {
							throw new ProductNotFoundException();
						}
				);
	}

	@Override
	@Transactional
	public Product associateProductToCategory (UUID productId, UUID categoryId) {
		Category category = categoryRepository.findCategoryById(categoryId)
				.orElseThrow(() -> new CategoryNotFoundException("Category was not found."));

		Product product = productRepository.findProductById(productId)
				.orElseThrow(() -> new ProductNotFoundException());

		if (product.getCategory() != null)
			throw new CategoryAlreadyExistsException("Category already exists in product.");

		category.getProducts().add(product);

		product.setCategory(category);

		return productRepository.saveProduct(product);
	}

	@Override
	public Category getProductCategory (UUID productId) {
		Product product = productValidator.validateIfProductExistsAndReturnTheExistingProduct(productId);

		return product.getCategory();
	}

	@Override
	public Product removeProductFromCategory (UUID productId) {
		return productRepository.findProductById(productId)
				.map(existingProduct -> {
					if (existingProduct.getCategory() == null)
						throw new CategoryNotFoundException("Product category cannot be null.");

					existingProduct.setCategory(null);

					return productRepository.saveProduct(existingProduct);
				}).orElseThrow(() -> new ProductNotFoundException());
	}

	@Override
	public void uploadProductImage (UUID productId, List<String> imagesUrl) throws IOException {
		Product product = productValidator.validateIfProductExistsAndReturnTheExistingProduct(productId);

		List<String> existingImages = product.getImagesUrl() != null
				? new ArrayList<>(product.getImagesUrl())
				: new ArrayList<>();

		imagesUrl.forEach(imageUrl -> {
			try {
				uploadImageToCloudinary(imageUrl, productId);
				existingImages.add(imageUrl);
			} catch (IOException e) {
				throw new RuntimeException("Error when upload image: " + imageUrl, e);
			}
		});

		product.setImagesUrl(existingImages);

		productRepository.saveProduct(product);
	}

	private void uploadImageToCloudinary (String imageUrl, UUID productId) throws IOException {
		try {
			cloudinaryServiceImplementation.uploadToImageCloudinary(imageUrl, productId);
			log.info("Image successfully uploaded to Cloudinary with url: {}", imageUrl);
		} catch (IOException exception) {
			log.error("Error uploading image to Cloudinary: {}", exception.getMessage());
			throw exception;
		}
	}

	@Override
	public List<String> getProductImages (UUID productId) {
		Product product = productValidator.validateIfProductExistsAndReturnTheExistingProduct(productId);
		return product.getImagesUrl();
	}

	@Override
	public void deleteProductImage (UUID productId) throws IOException, ProductImageNotFoundException {
		Product product = productValidator.validateIfProductExistsAndReturnTheExistingProduct(productId);

		if (product.getImagesUrl().isEmpty() || product.getImagesUrl() == null)
			throw new ProductImageNotFoundException("Product image was not found.");

		cloudinaryServiceImplementation.deleteImageFromCloudinary(productId);

		product.setImagesUrl(null);

		productRepository.saveProduct(product);

		log.info("Image removed from product {} and deleted from Cloudinary.", productId);
	}

	@Override
	public void increaseProductStock (UUID productId, int quantityToIncrease) {
		Product product = productValidator.validateIfProductExistsAndReturnTheExistingProduct(productId);

		int actualStock = product.getStock();

		if (quantityToIncrease <= 0)
			throw new InvalidQuantityException("Quantity to increase must be greater than zero.");

		int stockAfterIncrease = actualStock + quantityToIncrease;

		product.setStock(stockAfterIncrease);

		productRepository.saveProduct(product);
	}

	@Override
	public void decreaseProductStock (UUID productId, int quantityToDecrease) {
		Product product = productValidator.validateIfProductExistsAndReturnTheExistingProduct(productId);

		int actualStock = product.getStock();

		if (quantityToDecrease <= 0)
			throw new InvalidQuantityException("Quantity to decrease must be greater than zero.");

		if (actualStock - quantityToDecrease < 0)
			throw new ProductOutOfStockException("Product stock cannot be negative.");

		int stockAfterDecrease = actualStock - quantityToDecrease;

		product.setStock(stockAfterDecrease);

		productRepository.saveProduct(product);
	}

	@Override
	public int getProductStock (UUID productId) {
		Product product = productValidator.validateIfProductExistsAndReturnTheExistingProduct(productId);

		return product.getStock();
	}

	@Override
	public List<Product> filterProducts (ProductFiltersCriteria criteria) {
		if (criteria == null)
			throw new NullParametersException("The parameters can't be null.");
		return productRepository.filterProducts(criteria);
	}

}
