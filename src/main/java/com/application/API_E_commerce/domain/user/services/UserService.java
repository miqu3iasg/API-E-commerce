package com.application.API_E_commerce.domain.user.services;

import com.application.API_E_commerce.adapters.inbound.dtos.CreateUserRequestDTO;
import com.application.API_E_commerce.adapters.inbound.dtos.UpdateAddressRequestDTO;
import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.address.useCase.AddressUseCase;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.UserRole;
import com.application.API_E_commerce.domain.user.repository.UserRepositoryPort;
import com.application.API_E_commerce.domain.user.useCase.UserUseCase;
import com.application.API_E_commerce.infrastructure.exceptions.user.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserUseCase {

	private final UserRepositoryPort userRepositoryPort;
	private final AddressUseCase addressService;

	public UserService (UserRepositoryPort userRepositoryPort, AddressUseCase addressService) {
		this.userRepositoryPort = userRepositoryPort;
		this.addressService = addressService;
	}

	@Override
	@Transactional
	public User createUser (CreateUserRequestDTO createUserRequest) {
		validateUserPassword(createUserRequest.password());
		validateUserRole(createUserRequest.role().name());

		User user = new User();
		user.setName(createUserRequest.name());
		user.setEmail(createUserRequest.email());
		user.setPassword(createUserRequest.password());
		user.setRole(createUserRequest.role());
		user.setCreatedAt(LocalDateTime.now());

		Address address = buildAddress(createUserRequest);

		user.setAddress(address);

		return userRepositoryPort.saveUser(user);
	}

	private void validateUserPassword (String password) {
		if (password.length() < 8) throw new InvalidUserPasswordException();

		if (password.trim().isEmpty())
			throw new MissingUserPasswordException("Password cannot be empty.");
	}

	private void validateUserRole (String role) {
		if (role == null || role.trim().isEmpty())
			throw new MissingUserRoleException("Role cannot be empty.");
		try {
			UserRole.valueOf(role.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new InvalidUserRoleException();
		}
	}

	private static Address buildAddress (CreateUserRequestDTO createUserRequest) {
		Address address = new Address();
		address.setCity(createUserRequest.address().city());
		address.setStreet(createUserRequest.address().street());
		address.setCity(createUserRequest.address().city());
		address.setState(createUserRequest.address().state());
		address.setZipCode(createUserRequest.address().zipCode());
		address.setCountry(createUserRequest.address().country());
		return address;
	}

	@Override
	public Optional<User> findUserById (UUID userId) {
		return userRepositoryPort.findUserById(userId);
	}

	@Override
	public User updateUserName (UUID userId, String updatedNameRequest) {
		return userRepositoryPort.findUserById(userId)
				.map(existingUser -> {
					existingUser.setName(updatedNameRequest);
					userRepositoryPort.saveUser(existingUser);
					return existingUser;
				}).orElseThrow(() -> new UserNotFoundException("Cannot update the " +
						"name because " +
						"the user was not found."));
	}

	@Override
	public void updatedUserPassword (UUID userId, String updatedPasswordRequest) {
		userRepositoryPort.findUserById(userId)
				.map(existingUser -> {
					existingUser.setPassword(updatedPasswordRequest);
					userRepositoryPort.saveUser(existingUser);
					return existingUser;
				}).orElseThrow(() -> new UserNotFoundException("Cannot update the " +
						"password because the user was not found."));
	}

	@Override
	public Address updateUserAddress (UUID userId, UpdateAddressRequestDTO updatedAddressRequest) {
		return userRepositoryPort.findUserById(userId)
				.map(existingUser -> {
					existingUser.getAddress().setStreet(updatedAddressRequest.street());
					existingUser.getAddress().setCity(updatedAddressRequest.city());
					existingUser.getAddress().setState(updatedAddressRequest.state());
					existingUser.getAddress().setZipCode(updatedAddressRequest.zipCode());
					existingUser.getAddress().setCountry(updatedAddressRequest.country());

					userRepositoryPort.saveUser(existingUser);

					return existingUser.getAddress();
				}).orElseThrow(() -> new UserNotFoundException("Cannot update the address because " +
						"the user was not found."));
	}

	@Override
	public void deleteUser (UUID userId) {
		userRepositoryPort.findUserById(userId)
				.map(userFound -> {
					userRepositoryPort.deleteUserById(userId);
					return userFound;
				});
	}

	@Override
	public List<User> findAllUsers () {
		return userRepositoryPort.findAllUsers();
	}

}
