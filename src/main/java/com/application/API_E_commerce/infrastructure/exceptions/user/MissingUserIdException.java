package com.application.API_E_commerce.infrastructure.exceptions.user;

import com.application.API_E_commerce.infrastructure.exceptions.NullParametersException;

public class MissingUserIdException extends NullParametersException {

	private static final String DEFAULT_MESSAGE = "The user id cannot be null.";

	public MissingUserIdException () { super(DEFAULT_MESSAGE); }

	public MissingUserIdException (String message) {
		super(message);
	}

}
