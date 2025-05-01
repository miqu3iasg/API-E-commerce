package com.application.API_E_commerce.infrastructure.exceptions.user;

public class InvalidUserPasswordException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Invalid password, the password must have at least 8 characters";

	public InvalidUserPasswordException () {
		super(DEFAULT_MESSAGE);
	}

	public InvalidUserPasswordException (String message) {
		super(message);
	}

	public InvalidUserPasswordException (String message, Throwable cause) {
		super(message, cause);
	}

}
