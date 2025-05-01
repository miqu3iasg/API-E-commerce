package com.application.API_E_commerce.infrastructure.exceptions.user;

public class InvalidUserRoleException extends RuntimeException{
	private static final String DEFAULT_MESSAGE = "Invalid role, the role must be admin or customer";

	public InvalidUserRoleException () {
		super(DEFAULT_MESSAGE);
	}

	public InvalidUserRoleException (String message) {
		super(message);
	}

	public InvalidUserRoleException (String message, Throwable cause) {
		super(message, cause);
	}

}
