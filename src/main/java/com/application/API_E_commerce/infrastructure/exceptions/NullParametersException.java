package com.application.API_E_commerce.infrastructure.exceptions;

public class NullParametersException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "The parameters cannot be " +
			"null.";

	public NullParametersException () { super(DEFAULT_MESSAGE); }

	public NullParametersException (String message, Throwable cause) {
		super(message, cause);
	}

	public NullParametersException (String message) {
		super(message);
	}

}
