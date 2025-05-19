package com.application.API_E_commerce.domain.image.gateways;

import java.io.IOException;
import java.util.UUID;

public interface ImageStorageGateway {
	String uploadImage(String imageUrl, UUID productId) throws IOException;

	void deleteImage(UUID productId) throws IOException;
}