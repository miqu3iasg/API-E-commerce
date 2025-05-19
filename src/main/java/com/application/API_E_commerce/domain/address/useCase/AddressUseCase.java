package com.application.API_E_commerce.domain.address.useCase;

import com.application.API_E_commerce.adapters.inbound.dtos.CreateAddressRequestDTO;
import com.application.API_E_commerce.adapters.inbound.dtos.UpdateAddressRequestDTO;
import com.application.API_E_commerce.domain.address.Address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressUseCase {

	Address createAddress (CreateAddressRequestDTO request);

	Address updateAddress (UUID id, UpdateAddressRequestDTO request);

	void deleteAddress (UUID id);

	List<Address> findAllAddresses ();

	Optional<Address> findAddressById (UUID id);

}
