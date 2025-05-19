package com.application.API_E_commerce.adapters.outbound.repositories;

import com.application.API_E_commerce.adapters.outbound.entities.address.JpaAddressEntity;
import com.application.API_E_commerce.adapters.outbound.repositories.jpa.JpaAddressRepository;
import com.application.API_E_commerce.common.utils.mappers.AddressMapper;
import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.address.repository.AddressRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AddressRepositoryImplementation implements AddressRepositoryPort {

	private final JpaAddressRepository jpaAddressRepository;
	private final AddressMapper addressMapper;

	public AddressRepositoryImplementation (JpaAddressRepository jpaAddressRepository, AddressMapper addressMapper) {
		this.jpaAddressRepository = jpaAddressRepository;
		this.addressMapper = addressMapper;
	}

	@Override
	public Address saveAddress (Address address) {
		JpaAddressEntity jpaAddressEntityToSave = addressMapper.toJpa(address);

		JpaAddressEntity jpaAddressEntitySaved = jpaAddressRepository.save(jpaAddressEntityToSave);

		return addressMapper.toDomain(jpaAddressEntitySaved);
	}

	@Override
	public Optional<Address> findAddressById (UUID id) {
		return Optional.empty();
	}

	@Override
	public void deleteAddressById (UUID id) {

	}

	@Override
	public List<Address> findAllAddresses () {
		return List.of();
	}

}
