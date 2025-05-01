package com.application.API_E_commerce.infrastructure.exceptions.cart;

public class UserCartNotFoundException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "User cart not found";

	public UserCartNotFoundException () { super(DEFAULT_MESSAGE); }

	public UserCartNotFoundException (String message, Throwable cause) {
		super(message, cause);
	}

	public UserCartNotFoundException (String message) {
		super(message);
	}

}
