package com.application.API_E_commerce.infrastructure.exceptions.payment;

public class InvalidPaymentMethodException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Invalid payment method";

	public InvalidPaymentMethodException () { super(DEFAULT_MESSAGE); }

	public InvalidPaymentMethodException (String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPaymentMethodException (String message) {
		super(message);
	}

}
