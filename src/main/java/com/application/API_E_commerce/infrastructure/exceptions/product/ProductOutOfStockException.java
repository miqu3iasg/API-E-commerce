package com.application.API_E_commerce.infrastructure.exceptions.product;

public class ProductOutOfStockException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Product out of stock";

	public ProductOutOfStockException () { super(DEFAULT_MESSAGE); }

	public ProductOutOfStockException (String message, Throwable cause) {
		super(message, cause);
	}

	public ProductOutOfStockException (String message) {
		super(message);
	}

}
