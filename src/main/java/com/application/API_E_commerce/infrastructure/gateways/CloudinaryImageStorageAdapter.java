package com.application.API_E_commerce.infrastructure.gateways;

import com.application.API_E_commerce.domain.image.gateways.ImageStorageGateway;
import com.application.API_E_commerce.infrastructure.exceptions.NullParametersException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.utils.StringUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class CloudinaryImageStorageAdapter implements ImageStorageGateway {

	private static final Logger log = LoggerFactory.getLogger(CloudinaryImageStorageAdapter.class);

	private final Cloudinary cloudinary;

	public CloudinaryImageStorageAdapter () {
		Dotenv dotenv = Dotenv.configure().load();

		String cloudinaryUrl = dotenv.get("CLOUDINARY_URL");

		if (cloudinaryUrl == null || cloudinaryUrl.isEmpty())
			throw new IllegalArgumentException("CLOUDINARY_URL is not set in the environment variables.");

		cloudinary = new Cloudinary(cloudinaryUrl);
	}

	@Override
	public String uploadImage (String imageUrl, UUID productId) throws IOException {
		validateInputs(imageUrl, productId);

		String publicId = "product_" + productId;

		try {
			Map<String, Object> params = ObjectUtils.asMap(
					"public_id", publicId,
					"overwrite", true
			);

			cloudinary.uploader().upload(imageUrl, params);

			log.info("Image successfully uploaded to Cloudinary with url: {}", imageUrl);

		} catch (IOException exception) {
			log.error("Error in upload image for Cloudinary: ", exception);
			throw exception;
		}
		return imageUrl;
	}

	private void validateInputs (String imageUrl, UUID productId) {
		if (imageUrl == null || imageUrl.trim().isEmpty() || productId == null)
			throw new NullParametersException("Image URL and Product ID must not be null or empty");
	}

	@Override
	public void deleteImage (UUID productId) throws IOException {
		String publicId = "product_" + productId;

		if (StringUtils.isEmpty(publicId))
			throw new IllegalArgumentException("Could not extract Public ID from image URL.");

		try {
			Map<String, Object> params = ObjectUtils.asMap("invalidate", true);

			var result = cloudinary.uploader().destroy(publicId, params);

			if (! "ok".equals(result.get("result")))
				throw new IOException("Failed to delete image from Cloudinary: " + result);

			log.info("Image successfully deleted from Cloudinary: {}", publicId);

		} catch (IOException exception) {
			log.error("Error deleting image from Cloudinary: ", exception);
			throw exception;
		}
	}

}
