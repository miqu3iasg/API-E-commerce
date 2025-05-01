package com.application.API_E_commerce.infrastructure.exceptions.category;

import com.application.API_E_commerce.infrastructure.exceptions.NullParametersException;

public class MissingCategoryDescriptionException extends NullParametersException {

	private static final String DEFAULT_MESSAGE = "The category description cannot be null.";

	public MissingCategoryDescriptionException () {
		super(DEFAULT_MESSAGE);
	}

	public MissingCategoryDescriptionException (String message, Throwable cause) {
		super(message, cause);
	}


	public MissingCategoryDescriptionException (String message) {
		super(message);
	}

}
