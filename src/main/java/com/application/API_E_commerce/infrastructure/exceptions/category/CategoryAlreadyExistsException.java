package com.application.API_E_commerce.infrastructure.exceptions.category;

public class CategoryAlreadyExistsException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Category already exists.";

	public CategoryAlreadyExistsException () {
		super(DEFAULT_MESSAGE);
	}

	public CategoryAlreadyExistsException (String message, Throwable cause) {
		super(message, cause);
	}

	public CategoryAlreadyExistsException (String message) {
		super(message);
	}

}
