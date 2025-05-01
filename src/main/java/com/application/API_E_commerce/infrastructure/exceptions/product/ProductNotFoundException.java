package com.application.API_E_commerce.infrastructure.exceptions.product;

public class ProductNotFoundException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Product not found";

	public ProductNotFoundException () { super(DEFAULT_MESSAGE); }

	public ProductNotFoundException (String message, Throwable cause) {
		super(message, cause);
	}

	public ProductNotFoundException (String message) {
		super(message);
	}

}
