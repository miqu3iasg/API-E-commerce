package com.application.API_E_commerce.infrastructure.exceptions.product;

public class InvalidQuantityException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Invalid quantity";

	public InvalidQuantityException () { super(DEFAULT_MESSAGE); }

	public InvalidQuantityException (String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidQuantityException (String message) {
		super(message);
	}

}
