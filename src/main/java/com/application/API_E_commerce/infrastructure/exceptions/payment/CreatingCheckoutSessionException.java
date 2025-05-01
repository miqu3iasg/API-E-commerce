package com.application.API_E_commerce.infrastructure.exceptions.payment;

import com.stripe.exception.StripeException;

public class CreatingCheckoutSessionException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Creating checkout session failed";

	public CreatingCheckoutSessionException () { super(DEFAULT_MESSAGE); }

	public CreatingCheckoutSessionException (String message) {
		super(message);
	}

	public CreatingCheckoutSessionException (String message, StripeException e) {
		super(message, e);
	}

}
