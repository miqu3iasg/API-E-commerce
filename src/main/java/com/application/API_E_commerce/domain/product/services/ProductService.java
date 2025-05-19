package com.application.API_E_commerce.domain.product.services;

import com.application.API_E_commerce.adapters.inbound.dtos.CreateProductRequestDTO;
import com.application.API_E_commerce.adapters.inbound.dtos.ProductFiltersCriteria;
import com.application.API_E_commerce.adapters.inbound.dtos.UpdateProductRequestDTO;
import com.application.API_E_commerce.common.utils.validators.CategoryValidator;
import com.application.API_E_commerce.common.utils.validators.ProductValidator;
import com.application.API_E_commerce.domain.category.Category;
import com.application.API_E_commerce.domain.category.repository.CategoryRepositoryPort;
import com.application.API_E_commerce.domain.image.gateways.ImageStorageGateway;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.repository.ProductRepositoryPort;
import com.application.API_E_commerce.domain.product.useCase.ProductUseCase;
import com.application.API_E_commerce.infrastructure.exceptions.NullParametersException;
import com.application.API_E_commerce.infrastructure.exceptions.category.CategoryAlreadyExistsException;
import com.application.API_E_commerce.infrastructure.exceptions.category.CategoryNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.product.ProductImageNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.product.ProductNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ProductService implements ProductUseCase {

	private static final Logger log = LoggerFactory.getLogger(ProductService.class);

	private final ProductRepositoryPort productRepositoryPort;
	private final ProductValidator productValidator;
	private final CategoryValidator categoryValidator;
	private final CategoryRepositoryPort categoryRepositoryPort;
	private final ImageStorageGateway imageStorageGateway;

	public ProductService (
			ProductRepositoryPort productRepositoryPort,
			ProductValidator productValidator,
			CategoryValidator categoryValidator,
			CategoryRepositoryPort categoryRepositoryPort,
			ImageStorageGateway imageStorageGateway
	) {
		this.productRepositoryPort = productRepositoryPort;
		this.productValidator = productValidator;
		this.categoryValidator = categoryValidator;
		this.categoryRepositoryPort = categoryRepositoryPort;
		this.imageStorageGateway = imageStorageGateway;
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

		return productRepositoryPort.saveProduct(product);
	}

	@Override
	public Optional<Product> findProductById (UUID productId) {
		return productRepositoryPort.findProductById(productId);
	}

	@Override
	public List<Product> findAllProducts () {
		return productRepositoryPort.findAllProducts();
	}

	@Override
	public Product updateProduct (UUID productId, UpdateProductRequestDTO updateProductRequest) {
		return productRepositoryPort.findProductById(productId)
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
					return productRepositoryPort.saveProduct(existingProduct);
				})
				.orElseThrow(() -> new ProductNotFoundException());
	}

	@Override
	public void deleteProduct (UUID productId) {
		productRepositoryPort.findProductById(productId)
				.ifPresentOrElse(
						existingProduct -> productRepositoryPort.deleteProductById(productId),
						() -> {
							throw new ProductNotFoundException();
						}
				);
	}

	@Override
	@Transactional
	public Product associateProductToCategory (UUID productId, UUID categoryId) {
		Category category = categoryRepositoryPort.findCategoryById(categoryId)
				.orElseThrow(() -> new CategoryNotFoundException("Category was not found."));

		Product product = productRepositoryPort.findProductById(productId)
				.orElseThrow(() -> new ProductNotFoundException());

		if (product.getCategory() != null)
			throw new CategoryAlreadyExistsException("Category already exists in product.");

		category.getProducts().add(product);

		product.setCategory(category);

		return productRepositoryPort.saveProduct(product);
	}

	@Override
	public Category getProductCategory (UUID productId) {
		Product product = productValidator.validateIfProductExistsAndReturnTheExistingProduct(productId);

		return product.getCategory();
	}

	@Override
	public Product removeProductFromCategory (UUID productId) {
		return productRepositoryPort.findProductById(productId)
				.map(existingProduct -> {
					if (existingProduct.getCategory() == null)
						throw new CategoryNotFoundException("Product category cannot be null.");

					existingProduct.setCategory(null);

					return productRepositoryPort.saveProduct(existingProduct);
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

		productRepositoryPort.saveProduct(product);
	}

	private void uploadImageToCloudinary (String imageUrl, UUID productId) throws IOException {
		try {
			imageStorageGateway.uploadImage(imageUrl, productId);
			log.info("Image successfully uploaded to Cloudinary with url: {}",
					imageUrl);
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

		imageStorageGateway.deleteImage(productId);

		product.setImagesUrl(null);

		productRepositoryPort.saveProduct(product);

		log.info("Image removed from product {} and deleted from Cloudinary.", productId);
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
		return productRepositoryPort.filterProducts(criteria);
	}

}
