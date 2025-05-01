package com.application.API_E_commerce.infrastructure.exceptions.payment;

import com.application.API_E_commerce.infrastructure.exceptions.NullParametersException;

public class MissingPaymentRequestException extends NullParametersException {

	private static final String DEFAULT_MESSAGE = "The payment request cannot be null.";

	public MissingPaymentRequestException () { super(DEFAULT_MESSAGE); }

	public MissingPaymentRequestException (String message, Throwable cause) {
		super(message, cause);
	}

	public MissingPaymentRequestException (String message) {
		super(message);
	}

}
