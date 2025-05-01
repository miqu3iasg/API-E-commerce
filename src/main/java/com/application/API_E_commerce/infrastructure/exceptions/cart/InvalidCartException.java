package com.application.API_E_commerce.infrastructure.exceptions.cart;

public class InvalidCartException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Invalid cart.";

	public InvalidCartException () {
		super(DEFAULT_MESSAGE);
	}

	public InvalidCartException (String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidCartException (String message) {
		super(message);
	}

}
