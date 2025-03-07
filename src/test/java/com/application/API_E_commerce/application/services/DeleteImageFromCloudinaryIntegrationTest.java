package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.adapters.outbound.repositories.ProductRepositoryImplementation;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.dtos.CreateProductRequestDTO;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
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

    List<String> imageUrls = List.of(
            "https://media.istockphoto.com/id/496603666/pt/vetorial/%C3%ADcone-plana-verifica%C3%A7%C3%A3o.jpg?s=612x612&w=0&k=20&c=59xwMZUHiaI53N1ouEYGjVsdbanq4iXqiU_MppilZ7M="
    );

    String uploadedImageUrl = cloudinaryService.uploadToImageCloudinary(imageUrls.getFirst(), productId);

    productService.uploadProductImage(productId, imageUrls);

    Product updatedProduct = productRepository.findProductById(productId).orElseThrow();
    assertNotNull(updatedProduct.getImagesUrl(), "The URL was not saved.");
    assertEquals(imageUrls, updatedProduct.getImagesUrl(), "The image URL does not match.");

    productService.deleteProductImage(productId);

    log.info("Product image delete with success with id: {}", productId);

    Product finalProduct = productRepository.findProductById(productId).orElseThrow();
    assertTrue(finalProduct.getImagesUrl().isEmpty(), "URLs are still present in the database.");
  }
}
