package com.application.API_E_commerce.infrastructure.exceptions.product;

import com.application.API_E_commerce.infrastructure.exceptions.NullParametersException;

public class MissingProductIdException extends NullParametersException {

	private static final String DEFAULT_MESSAGE = "The product id cannot be null.";

	public MissingProductIdException () { super(DEFAULT_MESSAGE); }

	public MissingProductIdException (String message) {
		super(message);
	}

}
