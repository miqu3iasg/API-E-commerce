package com.application.API_E_commerce.domain.address.services;

import com.application.API_E_commerce.adapters.inbound.dtos.CreateAddressRequestDTO;
import com.application.API_E_commerce.adapters.inbound.dtos.UpdateAddressRequestDTO;
import com.application.API_E_commerce.common.utils.validators.AddressValidator;
import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.address.repository.AddressRepositoryPort;
import com.application.API_E_commerce.domain.address.useCase.AddressUseCase;
import com.application.API_E_commerce.infrastructure.exceptions.address.AddressNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.address.MissingAddressIdException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AddressService implements AddressUseCase {

	private final AddressRepositoryPort addressRepositoryPort;

	public AddressService (AddressRepositoryPort addressRepositoryPort) {
		this.addressRepositoryPort = addressRepositoryPort;
	}

	@Override
	@Transactional
	public Address createAddress (CreateAddressRequestDTO request) {
		AddressValidator.validate(request);

		Address address = new Address();
		address.setCity(request.city());
		address.setStreet(request.street());
		address.setCity(request.city());
		address.setState(request.state());
		address.setZipCode(request.zipCode());
		address.setCountry(request.country());

		return addressRepositoryPort.saveAddress(address);
	}

	@Override
	public Address updateAddress (UUID id, UpdateAddressRequestDTO request) {
		validateInput(id);
		AddressValidator.validate(request);

		return addressRepositoryPort.findAddressById(id)
				.map(existingAddress -> {
					existingAddress.setCity(request.city());
					existingAddress.setStreet(request.street());
					existingAddress.setCity(request.city());
					existingAddress.setState(request.state());
					existingAddress.setZipCode(request.zipCode());
					existingAddress.setCountry(request.country());

					return addressRepositoryPort.saveAddress(existingAddress);
				}).orElseThrow(() -> new AddressNotFoundException("Address not " +
						"found."));
	}

	private void validateInput (UUID id) {
		if (id == null) throw new MissingAddressIdException("The address id " +
				"cannot be null.");
	}

	@Override
	public void deleteAddress (UUID id) {
		validateInput(id);

		addressRepositoryPort.findAddressById(id)
				.map(existingAddress -> {
					addressRepositoryPort.deleteAddressById(id);
					return existingAddress;
				}).orElseThrow(() -> new AddressNotFoundException("Address not " +
						"found."));
	}

	@Override
	public List<Address> findAllAddresses () {
		return addressRepositoryPort.findAllAddresses();
	}

	@Override
	public Optional<Address> findAddressById (UUID id) {
		validateInput(id);
		return addressRepositoryPort.findAddressById(id);
	}

}
