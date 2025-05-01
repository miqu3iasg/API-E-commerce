package com.application.API_E_commerce.infrastructure.exceptions.payment;

import com.application.API_E_commerce.infrastructure.exceptions.NullParametersException;

public class MissingPaymentMethodException extends NullParametersException {

	private static final String DEFAULT_MESSAGE = "The payment method cannot be null.";

	public MissingPaymentMethodException () { super(DEFAULT_MESSAGE); }

	public MissingPaymentMethodException (String message, Throwable cause) {
		super(message, cause);
	}

	public MissingPaymentMethodException (String message) {
		super(message);
	}

}
