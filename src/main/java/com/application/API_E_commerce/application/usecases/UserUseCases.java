package com.application.API_E_commerce.application.usecases;

import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.address.dtos.UpdateAddressRequestDTO;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.dtos.CreateUserRequestDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserUseCases {
  User createUser(CreateUserRequestDTO createUserRequest);
  Optional<User> findUserById(UUID userId);
  void updateUserName(UUID userId, String updatedNameRequest);
  void updatedUserPassword(UUID userId, String updatedPasswordRequest);
  Address updateUserAddress(UUID userId, UpdateAddressRequestDTO updatedAddressRequest);
  void deleteUser(UUID userId);
  List<User> findAllUsers();
}
