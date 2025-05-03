package com.application.API_E_commerce.infrastructure.exceptions.address;

public class AddressNotFoundException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Address not found";

	public AddressNotFoundException () {
		super(DEFAULT_MESSAGE);
	}

	public AddressNotFoundException (String message, Throwable cause) {
		super(message, cause);
	}

	public AddressNotFoundException (String message) {
		super(message);
	}

}
