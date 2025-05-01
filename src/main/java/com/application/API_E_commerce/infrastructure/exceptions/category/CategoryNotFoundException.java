package com.application.API_E_commerce.infrastructure.exceptions.category;

public class CategoryNotFoundException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Category not found.";

	public CategoryNotFoundException () {
		super(DEFAULT_MESSAGE);
	}

	public CategoryNotFoundException (String message) {
		super(message);
	}

	public CategoryNotFoundException (String message, Throwable cause) {
		super(message, cause);
	}

}
