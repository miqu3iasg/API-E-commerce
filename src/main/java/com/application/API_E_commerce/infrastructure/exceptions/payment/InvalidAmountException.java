package com.application.API_E_commerce.infrastructure.exceptions.payment;

public class InvalidAmountException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Invalid amount";

	public InvalidAmountException () { super(DEFAULT_MESSAGE); }

	public InvalidAmountException (String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidAmountException (String message) {
		super(message);
	}

}
