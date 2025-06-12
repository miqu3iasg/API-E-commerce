package com.application.API_E_commerce.infrastructure.exceptions.payment;

public class MissingPaymentIntentIdException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Payment intent ID is missing.";

	public MissingPaymentIntentIdException (String message) {
		super(message);
	}

	public MissingPaymentIntentIdException () {
		super(DEFAULT_MESSAGE);
	}

	public MissingPaymentIntentIdException (Throwable cause) {
		super(DEFAULT_MESSAGE, cause);
	}

	public MissingPaymentIntentIdException (String message, Throwable cause) {
		super(message, cause);
	}

}
