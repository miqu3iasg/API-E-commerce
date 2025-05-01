package com.application.API_E_commerce.infrastructure.exceptions.product;

public class ProductImageNotFoundException extends Throwable {

	private static final String DEFAULT_MESSAGE = "Product image not found";

	public ProductImageNotFoundException () { super(DEFAULT_MESSAGE); }

	public ProductImageNotFoundException (String message, Throwable cause) {
		super(message, cause);
	}

	public ProductImageNotFoundException (String message) {
		super(message);
	}

}
