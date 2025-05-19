package com.application.API_E_commerce.factory;

import com.application.API_E_commerce.adapters.inbound.dtos.UpdateAddressRequestDTO;

public class UpdateAddressRequestFactory {

	public static UpdateAddressRequestDTO build () {
		return new UpdateAddressRequestDTO(
				"New Street",
				"New City",
				"New State",
				"54321",
				"New Country"
		);
	}

}
