package com.application.API_E_commerce.factory;

import com.application.API_E_commerce.adapters.outbound.entities.address.JpaAddressEntity;
import com.application.API_E_commerce.domain.address.Address;

public class JpaAddressEntityFactory {
  public static JpaAddressEntity build(Address address) {
    JpaAddressEntity jpaAddressEntity = new JpaAddressEntity();
    jpaAddressEntity.setId(address.getId());
    jpaAddressEntity.setStreet(address.getStreet());
    jpaAddressEntity.setCity(address.getCity());
    jpaAddressEntity.setState(address.getState());
    jpaAddressEntity.setZipCode(address.getZipCode());
    jpaAddressEntity.setCountry(address.getCountry());
    return jpaAddressEntity;
  }
}
