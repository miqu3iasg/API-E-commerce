package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.adapters.outbound.repositories.ProductRepositoryImplementation;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.dtos.CreateProductRequestDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class DeleteImageFromCloudinaryIntegrationTest {

  @Autowired
  private ProductServiceImplementation productService;

  @Autowired
  private ProductRepositoryImplementation productRepository;

  @Autowired
  private CloudinaryServiceImplementation cloudinaryService;

  private static final String IMAGE_NAME = "test-image";

  @Test
  void shouldDeleteImageFromCloudinaryWithPublicIdSuccessfully() throws IOException {
    CreateProductRequestDTO createProductRequest = new CreateProductRequestDTO(
            "Test Name",
            "Test description",
            BigDecimal.valueOf(20.0),
            10
    );

    Product product = productService.createProduct(createProductRequest);

    UUID productId = product.getId();

    String imageUrl = "https://media.istockphoto.com/id/496603666/pt/vetorial/%C3%ADcone-plana-verifica%C3%A7%C3%A3o.jpg?s=612x612&w=0&k=20&c=59xwMZUHiaI53N1ouEYGjVsdbanq4iXqiU_MppilZ7M=";

    String uploadedImageUrl = cloudinaryService.uploadToImageCloudinary(imageUrl, IMAGE_NAME);
    productService.uploadProductImage(productId, uploadedImageUrl, IMAGE_NAME);

    Product updatedProduct = productRepository.findProductById(productId).orElseThrow();
    assertNotNull(updatedProduct.getImageUrl(), "The url was not save.");
    assertEquals(uploadedImageUrl, updatedProduct.getImageUrl());

    productService.deleteProductImage(productId, uploadedImageUrl);

    Product finalProduct = productRepository.findProductById(productId).orElseThrow();
    assertNull(finalProduct.getImageUrl(), "Url is present in database.");

    assertThrows(IOException.class, () -> cloudinaryService.deleteImageFromCloudinary(uploadedImageUrl));
  }
}
