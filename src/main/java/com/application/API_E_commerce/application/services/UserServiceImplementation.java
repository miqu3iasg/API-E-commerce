package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.UserUseCases;
import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.address.AddressUseCases;
import com.application.API_E_commerce.domain.address.dtos.UpdateAddressRequestDTO;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.UserRole;
import com.application.API_E_commerce.domain.user.dtos.CreateUserRequestDTO;
import com.application.API_E_commerce.domain.user.repository.UserRepository;
import com.application.API_E_commerce.infrastructure.exceptions.user.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImplementation implements UserUseCases {

	private final UserRepository userRepository;
	private final AddressUseCases addressService;

	public UserServiceImplementation (UserRepository userRepository, AddressUseCases addressService) {
		this.userRepository = userRepository;
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

		// Chamar o address service sem deixar a entidade detach
		Address address = new Address();
		address.setCity(createUserRequest.address().city());
		address.setStreet(createUserRequest.address().street());
		address.setCity(createUserRequest.address().city());
		address.setState(createUserRequest.address().state());
		address.setZipCode(createUserRequest.address().zipCode());
		address.setCountry(createUserRequest.address().country());
		user.setAddress(address);

		return userRepository.saveUser(user);
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

	@Override
	public Optional<User> findUserById (UUID userId) {
		return userRepository.findUserById(userId);
	}

	@Override
	public User updateUserName (UUID userId, String updatedNameRequest) {
		return userRepository.findUserById(userId)
				.map(existingUser -> {
					existingUser.setName(updatedNameRequest);
					userRepository.saveUser(existingUser);
					return existingUser;
				}).orElseThrow(() -> new UserNotFoundException("Cannot update the " +
						"name because " +
						"the user was not found."));
	}

	@Override
	public void updatedUserPassword (UUID userId, String updatedPasswordRequest) {
		userRepository.findUserById(userId)
				.map(existingUser -> {
					existingUser.setPassword(updatedPasswordRequest);
					userRepository.saveUser(existingUser);
					return existingUser;
				}).orElseThrow(() -> new UserNotFoundException("Cannot update the " +
						"password because the user was not found."));
	}

	@Override
	public Address updateUserAddress (UUID userId, UpdateAddressRequestDTO updatedAddressRequest) {
		return userRepository.findUserById(userId)
				.map(existingUser -> {
					existingUser.getAddress().setStreet(updatedAddressRequest.street());
					existingUser.getAddress().setCity(updatedAddressRequest.city());
					existingUser.getAddress().setState(updatedAddressRequest.state());
					existingUser.getAddress().setZipCode(updatedAddressRequest.zipCode());
					existingUser.getAddress().setCountry(updatedAddressRequest.country());

					userRepository.saveUser(existingUser);

					return existingUser.getAddress();
				}).orElseThrow(() -> new UserNotFoundException("Cannot update the address because " +
						"the user was not found."));
	}

	@Override
	public void deleteUser (UUID userId) {
		userRepository.findUserById(userId)
				.map(userFound -> {
					userRepository.deleteUserById(userId);
					return userFound;
				});
	}

	@Override
	public List<User> findAllUsers () {
		return userRepository.findAllUsers();
	}

}
