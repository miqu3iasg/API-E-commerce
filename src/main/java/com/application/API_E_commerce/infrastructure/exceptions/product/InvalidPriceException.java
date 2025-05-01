package com.application.API_E_commerce.infrastructure.exceptions.product;

public class InvalidPriceException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Invalid price";

	public InvalidPriceException () { super(DEFAULT_MESSAGE); }

	public InvalidPriceException (String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPriceException (String message) {
		super(message);
	}

}
