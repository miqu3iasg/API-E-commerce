package com.application.API_E_commerce.infrastructure.exceptions.user;

import com.application.API_E_commerce.infrastructure.exceptions.NullParametersException;

public class MissingUserRoleException extends NullParametersException {

	private static final String DEFAULT_MESSAGE = "The user role cannot be null.";

	public MissingUserRoleException () { super(DEFAULT_MESSAGE); }

	public MissingUserRoleException (String message, Throwable cause) {
		super(message, cause);
	}

	public MissingUserRoleException (String message) {
		super(message);
	}

}
