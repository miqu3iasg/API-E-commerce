package com.application.API_E_commerce.application.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class CloudinaryService {
  private final Cloudinary cloudinary;

  public CloudinaryService() {
    Dotenv dotenv = Dotenv.configure().load();

    String cloudinaryUrl = dotenv.get("CLOUDINARY_URL");

    if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
      throw new IllegalArgumentException("CLOUDINARY_URL is not set in the environment variables.");
    }

    this.cloudinary = new Cloudinary(cloudinaryUrl); // erro aqui: Illegal character in authority at index 13: cloudinary://<722711591888539>:<avopkYNtPcFCKhpqFg9eDB3IQeM>@dbo8nfor3
  }

  public void uploadToImageCloudinary(String imageUrl, String imageName) throws IOException {
    this.validateInputs(imageUrl, imageName);

    try {
      var params = this.buildUploadParams(imageName);

      this.cloudinary.uploader().upload(imageUrl, params);

      log.info("Image successfully uploaded to Cloudinary with name: {}", imageName);

    } catch (IOException exception) {
      log.error("Error in upload image for Cloudinary: ", exception);
      throw exception;
    }
  }

  private void validateInputs(String imageUrl, String imageName) {
    if (imageUrl == null || imageUrl.trim().isEmpty() || imageName == null || imageName.trim().isEmpty()) {
      throw new IllegalArgumentException("Image URL and Image Name must not be null or empty");
    }
  }

  private Map<String, Object> buildUploadParams(String imageName) {
    return ObjectUtils.asMap(
            "public_id", imageName,
            "overwrite", true
    );
  }
}
