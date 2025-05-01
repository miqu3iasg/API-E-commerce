package com.application.API_E_commerce.domain.address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository {

  Address saveAddress ( Address address );

  Optional<Address> findAddressById ( UUID id );

  void deleteAddressById ( UUID id );

  List<Address> findAllAddresses ();

}
