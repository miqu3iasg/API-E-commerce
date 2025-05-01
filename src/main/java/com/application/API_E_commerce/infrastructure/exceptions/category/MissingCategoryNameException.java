package com.application.API_E_commerce.infrastructure.exceptions.category;

import com.application.API_E_commerce.infrastructure.exceptions.NullParametersException;

public class MissingCategoryNameException extends NullParametersException {

	private static final String DEFAULT_MESSAGE = "The category name cannot be null.";

	public MissingCategoryNameException () {
		super(DEFAULT_MESSAGE);
	}

	public MissingCategoryNameException (String message, Throwable cause) {
		super(message, cause);
	}

	public MissingCategoryNameException (String message) {
		super(message);
	}

}
