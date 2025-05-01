package com.application.API_E_commerce.infrastructure.exceptions.order;

public class OrderProcessingException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "An error occurred while processing the order";

	public OrderProcessingException () {
		super(DEFAULT_MESSAGE);
	}

	public OrderProcessingException (String message) {
		super(message);
	}

	public OrderProcessingException (String message, Throwable cause) {
		super(message, cause);
	}

}
