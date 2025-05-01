package com.application.API_E_commerce.infrastructure.exceptions.order;

public class EmptyOrderException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Order cannot be empty";

	public EmptyOrderException () {
		super(DEFAULT_MESSAGE);
	}

	public EmptyOrderException (String message, Throwable cause) {
		super(message, cause);
	}

	public EmptyOrderException (String message) {
		super(message);
	}

}
