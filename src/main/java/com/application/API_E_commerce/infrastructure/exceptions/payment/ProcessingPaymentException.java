package com.application.API_E_commerce.infrastructure.exceptions.payment;

import com.stripe.exception.StripeException;

public class ProcessingPaymentException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Processing payment failed";

	public ProcessingPaymentException () { super(DEFAULT_MESSAGE); }

	public ProcessingPaymentException (String message) {
		super(message);
	}

	public ProcessingPaymentException (String message, StripeException e) {
		super(message, e);
	}

}
