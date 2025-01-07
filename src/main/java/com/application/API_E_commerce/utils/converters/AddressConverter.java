package com.application.API_E_commerce.utils.converters;

import com.application.API_E_commerce.adapters.outbound.entities.address.JpaAddressEntity;
import com.application.API_E_commerce.domain.address.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressConverter {
  public JpaAddressEntity toJpa(Address domain) {
    if (domain == null) return null;

    JpaAddressEntity jpaAddressEntity = new JpaAddressEntity();
    jpaAddressEntity.setId(domain.getId());
    jpaAddressEntity.setStreet(domain.getStreet());
    jpaAddressEntity.setCity(domain.getCity());
    jpaAddressEntity.setState(domain.getState());
    jpaAddressEntity.setZipCode(domain.getZipCode());
    jpaAddressEntity.setCountry(domain.getCountry());

    return jpaAddressEntity;
  }

  public Address toDomain(JpaAddressEntity jpaAddressEntity) {
    if (jpaAddressEntity == null) return null;

    Address addressDomain = new Address();
    addressDomain.setId(jpaAddressEntity.getId());
    addressDomain.setStreet(jpaAddressEntity.getStreet());
    jpaAddressEntity.setCity(jpaAddressEntity.getCity());
    jpaAddressEntity.setState(jpaAddressEntity.getState());
    jpaAddressEntity.setZipCode(jpaAddressEntity.getZipCode());
    jpaAddressEntity.setCountry(jpaAddressEntity.getCountry());

    return addressDomain;
  }
}
