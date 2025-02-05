package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.ProductUseCases;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.dtos.CreateProductRequestDTO;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class UploadImageToCloudinaryIntegrationTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ProductUseCases productService;

  @Test
  void shouldUploadImageToCloudinaryAndUpdateProduct() throws IOException {
    CreateProductRequestDTO createProductRequest = new CreateProductRequestDTO(
            "Test Name",
            "Test description",
            BigDecimal.valueOf(20.0),
            10
    );

    Product product = productService.createProduct(createProductRequest);

    UUID productId = product.getId();
    String testImageUrl = "https://media.istockphoto.com/id/496603666/pt/vetorial/%C3%ADcone-plana-verifica%C3%A7%C3%A3o.jpg?s=612x612&w=0&k=20&c=59xwMZUHiaI53N1ouEYGjVsdbanq4iXqiU_MppilZ7M=";

    this.productService.uploadProductImage(productId, testImageUrl);

    Product updatedProduct = productRepository.findProductById(productId).orElseThrow();
    assertNotNull(updatedProduct.getImageUrl(), "Image URL should be updated");
    assertEquals(testImageUrl, updatedProduct.getImageUrl());
  }
}
