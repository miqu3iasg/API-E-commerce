package com.application.API_E_commerce.domain.address.repository;

import com.application.API_E_commerce.domain.address.Address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepositoryPort {

	Address saveAddress (Address address);

	Optional<Address> findAddressById (UUID id);

	void deleteAddressById (UUID id);

	List<Address> findAllAddresses ();

}
