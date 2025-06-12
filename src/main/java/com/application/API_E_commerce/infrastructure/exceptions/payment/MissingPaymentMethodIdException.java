package com.application.API_E_commerce.infrastructure.exceptions.payment;

public class MissingPaymentMethodIdException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Payment method ID is missing";

	public MissingPaymentMethodIdException (String message) {
		super(message);
	}

	public MissingPaymentMethodIdException () {
		super(DEFAULT_MESSAGE);
	}

	public MissingPaymentMethodIdException (Throwable cause) {
		super(DEFAULT_MESSAGE, cause);
	}

	public MissingPaymentMethodIdException (String message, Throwable cause) {
		super(message, cause);
	}

}
