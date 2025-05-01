package com.application.API_E_commerce.infrastructure.exceptions.user;

import com.application.API_E_commerce.infrastructure.exceptions.NullParametersException;

public class MissingUserPasswordException extends NullParametersException {

	private static final String DEFAULT_MESSAGE = "The user password cannot be null.";

	public MissingUserPasswordException () { super(DEFAULT_MESSAGE); }

	public MissingUserPasswordException (String message, Throwable cause) {
		super(message, cause);
	}

	public MissingUserPasswordException (String message) {
		super(message);
	}

}
