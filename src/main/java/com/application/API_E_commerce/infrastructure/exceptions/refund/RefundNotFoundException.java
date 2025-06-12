package com.application.API_E_commerce.infrastructure.exceptions.refund;

public class RefundNotFoundException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Refund not found";

	public RefundNotFoundException (String message, Exception cause) {
		super(message, cause);
	}

	public RefundNotFoundException (Exception cause) {
		super(DEFAULT_MESSAGE, cause);
	}

	public RefundNotFoundException (String message) {
		super(message);
	}

	public RefundNotFoundException () {
		super(DEFAULT_MESSAGE);
	}

}
