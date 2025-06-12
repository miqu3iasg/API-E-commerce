package com.application.API_E_commerce.infrastructure.exceptions.refund;

public class ProcessingRefundException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "An error occurred while processing the refund";

	public ProcessingRefundException (String message, Exception cause) {
		super(message, cause);
	}

	public ProcessingRefundException (Exception cause) {
		super(DEFAULT_MESSAGE, cause);
	}

	public ProcessingRefundException (String message) {
		super(message);
	}

	public ProcessingRefundException () {
		super(DEFAULT_MESSAGE);
	}

}
