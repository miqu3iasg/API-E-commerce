package com.application.API_E_commerce.infrastructure.exceptions.product;

public class InvalidOperationTypeException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Invalid operation type";

	public InvalidOperationTypeException () { super(DEFAULT_MESSAGE); }

	public InvalidOperationTypeException (String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidOperationTypeException (String message) {
		super(message);
	}

}
