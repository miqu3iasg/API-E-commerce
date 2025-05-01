package com.application.API_E_commerce.domain.address;

import com.application.API_E_commerce.domain.address.dtos.CreateAddressRequestDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressUseCases {

  Address createAddress ( CreateAddressRequestDTO request );

  Address updateAddress ( UUID id, UpdateAddressRequestDTO request );

  void deleteAddress ( UUID id );

  List<Address> findAllAddresses ();

  Optional<Address> findAddressById ( UUID id );

}
