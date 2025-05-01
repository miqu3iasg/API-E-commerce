package com.application.API_E_commerce.infrastructure.exceptions.order;

public class OrderNotFoundException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Order not found";

	public OrderNotFoundException () {
		super(DEFAULT_MESSAGE);
	}

	public OrderNotFoundException (String message, Throwable cause) {
		super(message, cause);
	}

	public OrderNotFoundException (String message) {
		super(message);
	}

}
