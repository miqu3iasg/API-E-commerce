package com.application.API_E_commerce.domain.user.useCase;

import com.application.API_E_commerce.adapters.inbound.dtos.CreateUserRequestDTO;
import com.application.API_E_commerce.adapters.inbound.dtos.UpdateAddressRequestDTO;
import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserUseCase {

	User createUser (CreateUserRequestDTO createUserRequest);

	Optional<User> findUserById (UUID userId);

	User updateUserName (UUID userId, String updatedNameRequest);

	void updatedUserPassword (UUID userId, String updatedPasswordRequest);

	Address updateUserAddress (UUID userId, UpdateAddressRequestDTO updatedAddressRequest);

	void deleteUser (UUID userId);

	List<User> findAllUsers ();

}
