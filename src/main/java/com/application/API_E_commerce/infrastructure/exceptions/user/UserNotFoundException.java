package com.application.API_E_commerce.infrastructure.exceptions.user;

public class UserNotFoundException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "User not found";

	public UserNotFoundException () {
		super(DEFAULT_MESSAGE);
	}

	public UserNotFoundException (String message) {
		super(message);
	}

	public UserNotFoundException (String message, Throwable cause) {
		super(message, cause);
	}

}
