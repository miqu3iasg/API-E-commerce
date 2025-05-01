package com.application.API_E_commerce.infrastructure.exceptions.payment;

import com.application.API_E_commerce.infrastructure.exceptions.NullParametersException;

public class MissingPaymentCurrencyException extends NullParametersException {

	private static final String DEFAULT_MESSAGE = "The payment currency cannot be null.";

	public MissingPaymentCurrencyException () { super(DEFAULT_MESSAGE); }

	public MissingPaymentCurrencyException (String message, Throwable cause) {
		super(message, cause);
	}

	public MissingPaymentCurrencyException (String message) {
		super(message);
	}

}
