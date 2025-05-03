package com.application.API_E_commerce.utils.validators;

import com.application.API_E_commerce.domain.address.UpdateAddressRequestDTO;
import com.application.API_E_commerce.domain.address.dtos.CreateAddressRequestDTO;
import com.application.API_E_commerce.infrastructure.exceptions.NullParametersException;

public class AddressValidator {

	public static void validate (CreateAddressRequestDTO request) {
		if (request == null)
			throw new NullParametersException("Address request cannot be null.");

		validateCommonFields(request.street(), request.city(), request.state(), request.country(), request.zipCode());
	}

	private static void validateCommonFields (String street, String city, String state, String country, String zipCode) {
		validateField(street, "Street", 3, 100);
		validateField(city, "City", 2, 50);
		validateField(state, "State", 2, 50);
		validateField(country, "Country", 2, 50);
		validateZipCode(zipCode);
	}

	private static void validateField (String value, String fieldName, int minLength, int maxLength) {
		if (value == null || value.trim().isEmpty())
			throw new NullParametersException(fieldName + " cannot be null or " +
					"empty.");

		String trimmed = value.trim();

		if (trimmed.length() < minLength || trimmed.length() > maxLength)
			throw new IllegalArgumentException(fieldName + " must be between " + minLength + " and " + maxLength + " characters long.");

		if (! trimmed.matches("^[\\p{L}0-9\\s.,'-]+$"))
			throw new IllegalArgumentException(fieldName + " contains invalid characters.");
	}

	private static void validateZipCode (String zipCode) {
		if (zipCode == null || zipCode.trim().isEmpty())
			throw new IllegalArgumentException("Zip code cannot be null or empty.");

		String trimmed = zipCode.trim();

		if (! trimmed.matches("^\\d{5}-?\\d{3}$"))
			throw new IllegalArgumentException("Invalid zip code format. Expected: 00000-000 or 00000000.");
	}

	public static void validate (UpdateAddressRequestDTO request) {
		if (request == null)
			throw new NullParametersException("Address request cannot be null.");

		validateCommonFields(request.street(), request.city(), request.state(), request.country(), request.zipCode());
	}

}

