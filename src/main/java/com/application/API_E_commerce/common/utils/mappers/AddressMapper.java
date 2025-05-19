package com.application.API_E_commerce.common.utils.mappers;

import com.application.API_E_commerce.adapters.outbound.entities.address.JpaAddressEntity;
import com.application.API_E_commerce.domain.address.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AddressMapper {

	AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

	@Named("addressToJpa")
	default JpaAddressEntity addressToJpa (Address address) {
		return address != null
				? AddressMapper.INSTANCE.toJpa(address)
				: null;
	}

	@Mappings({
			@Mapping(source = "id", target = "id"),
			@Mapping(source = "street", target = "street"),
			@Mapping(source = "city", target = "city"),
			@Mapping(source = "state", target = "state"),
			@Mapping(source = "zipCode", target = "zipCode"),
			@Mapping(source = "country", target = "country")
	})
	JpaAddressEntity toJpa (Address domain);

	@Named("addressToDomain")
	default Address addressToDomain (JpaAddressEntity JpaAddress) {
		return JpaAddress != null
				? AddressMapper.INSTANCE.toDomain(JpaAddress)
				: null;
	}

	@Mappings({
			@Mapping(source = "id", target = "id"),
			@Mapping(source = "street", target = "street"),
			@Mapping(source = "city", target = "city"),
			@Mapping(source = "state", target = "state"),
			@Mapping(source = "zipCode", target = "zipCode"),
			@Mapping(source = "country", target = "country")
	})
	Address toDomain (JpaAddressEntity jpa);

}
