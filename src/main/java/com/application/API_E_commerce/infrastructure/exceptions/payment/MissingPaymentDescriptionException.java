package com.application.API_E_commerce.infrastructure.exceptions.payment;

import com.application.API_E_commerce.infrastructure.exceptions.NullParametersException;

public class MissingPaymentDescriptionException extends NullParametersException {

	private static final String DEFAULT_MESSAGE = "The payment description cannot be null.";

	public MissingPaymentDescriptionException () { super(DEFAULT_MESSAGE); }

	public MissingPaymentDescriptionException (String message, Throwable cause) {
		super(message, cause);
	}

	public MissingPaymentDescriptionException (String message) {
		super(message);
	}

}
