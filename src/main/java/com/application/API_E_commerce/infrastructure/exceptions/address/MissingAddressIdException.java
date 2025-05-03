package com.application.API_E_commerce.infrastructure.exceptions.address;

import com.application.API_E_commerce.infrastructure.exceptions.NullParametersException;

public class MissingAddressIdException extends NullParametersException {

	private static final String DEFAULT_MESSAGE = "The address id cannot be null.";

	public MissingAddressIdException () {
		super(DEFAULT_MESSAGE);
	}

	public MissingAddressIdException (String message, Throwable cause) {
		super(message, cause);
	}

	public MissingAddressIdException (String message) {
		super(message);
	}

}
