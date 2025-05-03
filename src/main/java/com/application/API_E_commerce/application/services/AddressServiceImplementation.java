package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.address.AddressRepository;
import com.application.API_E_commerce.domain.address.AddressUseCases;
import com.application.API_E_commerce.domain.address.UpdateAddressRequestDTO;
import com.application.API_E_commerce.domain.address.dtos.CreateAddressRequestDTO;
import com.application.API_E_commerce.infrastructure.exceptions.address.AddressNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.address.MissingAddressIdException;
import com.application.API_E_commerce.utils.validators.AddressValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AddressServiceImplementation implements AddressUseCases {

	private final AddressRepository addressRepository;

	public AddressServiceImplementation (AddressRepository addressRepository) {
		this.addressRepository = addressRepository;
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

		return addressRepository.saveAddress(address);
	}

	@Override
	public Address updateAddress (UUID id, UpdateAddressRequestDTO request) {
		validateInput(id);
		AddressValidator.validate(request);

		return addressRepository.findAddressById(id)
				.map(existingAddress -> {
					existingAddress.setCity(request.city());
					existingAddress.setStreet(request.street());
					existingAddress.setCity(request.city());
					existingAddress.setState(request.state());
					existingAddress.setZipCode(request.zipCode());
					existingAddress.setCountry(request.country());

					return addressRepository.saveAddress(existingAddress);
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

		addressRepository.findAddressById(id)
				.map(existingAddress -> {
					addressRepository.deleteAddressById(id);
					return existingAddress;
				}).orElseThrow(() -> new AddressNotFoundException("Address not " +
						"found."));
	}

	@Override
	public List<Address> findAllAddresses () {
		return addressRepository.findAllAddresses();
	}

	@Override
	public Optional<Address> findAddressById (UUID id) {
		validateInput(id);
		return addressRepository.findAddressById(id);
	}

}
