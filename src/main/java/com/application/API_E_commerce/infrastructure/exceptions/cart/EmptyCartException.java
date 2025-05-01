package com.application.API_E_commerce.infrastructure.exceptions.cart;

public class EmptyCartException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Cart cannot be empty";

	public EmptyCartException () { super(DEFAULT_MESSAGE); }

	public EmptyCartException (String message, Throwable cause) {
		super(message, cause);
	}

	public EmptyCartException (String message) {
		super(message);
	}

}
