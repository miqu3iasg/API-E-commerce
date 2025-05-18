package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.ProductUseCases;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.dtos.CreateProductRequestDTO;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class UploadImageToCloudinaryIntegrationTest {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductUseCases productService;

	@Test
	void shouldUploadImageToCloudinaryAndUpdateProduct () throws IOException {
		CreateProductRequestDTO createProductRequest = new CreateProductRequestDTO(
				"Test Name",
				"Test description",
				BigDecimal.valueOf(20.0),
				10
		);

		Product product = productService.createProduct(createProductRequest);

		UUID productId = product.getId();
		List<String> testImages = List.of(
				"https://media.istockphoto.com/id/496603666/pt/vetorial/%C3%ADcone-plana-verifica%C3%A7%C3%A3o.jpg?s=612x612&w=0&k=20&c=59xwMZUHiaI53N1ouEYGjVsdbanq4iXqiU_MppilZ7M=",
				"https://images.pexels.com/photos/2071882/pexels-photo-2071882.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
		);

		productService.uploadProductImage(productId, testImages);

		Product updatedProduct = productRepository.findProductById(productId).orElseThrow();

		assertNotNull(updatedProduct.getImagesUrl(), "Image URL list should not be null");
		assertEquals(2, updatedProduct.getImagesUrl().size(), "Product should have two images");
		assertTrue(updatedProduct.getImagesUrl().containsAll(testImages), "All uploaded images should be present");
	}

}
