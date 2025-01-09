package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.UserUseCases;
import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.address.dtos.UpdateAddressRequestDTO;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.UserRepository;
import com.application.API_E_commerce.domain.user.dtos.CreateUserRequestDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImplementation implements UserUseCases {
  private final UserRepository userRepository;

  public UserServiceImplementation(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void createUser(CreateUserRequestDTO createUserRequest) {
    this.validateUserUniqueness(createUserRequest);

    User user = new User();
    user.setName(createUserRequest.name());
    user.setEmail(createUserRequest.email());
    user.setPassword(createUserRequest.password());
    user.setRole(createUserRequest.role());
    user.setAddress(createUserRequest.address());
    user.setCreatedAt(LocalDateTime.now());

    this.userRepository.saveUser(user);
  }

  private void validateUserUniqueness(CreateUserRequestDTO createUserRequest) {
    Optional<User> existingUserByName = this.userRepository.findUserByName(createUserRequest.name());

    if (existingUserByName.isPresent()) {
      throw new IllegalArgumentException("A user with this name already exists.");
    }

    Optional<User> existingUserByEmail = this.userRepository.findUserByEmail(createUserRequest.email());

    if (existingUserByEmail.isPresent()) {
      throw new IllegalArgumentException("A user with this email already exists.");
    }
  }

  @Override
  public Optional<User> findUserById(UUID userId) {
    return userRepository.findUserById(userId);
  }

  @Override
  public void updateUserName(UUID userId, String updatedNameRequest) {
    this.userRepository.findUserById(userId)
            .map(existingUser -> {
              existingUser.setName(updatedNameRequest);
              return this.userRepository.saveUser(existingUser);
            }).orElseThrow(() -> new IllegalStateException("Cannot update the name because the user was not found."));
  }

  @Override
  public void updatedUserPassword(UUID userId, String updatedPasswordRequest) {
    this.userRepository.findUserById(userId)
            .map(existingUser -> {
              existingUser.setPassword(updatedPasswordRequest);
              return this.userRepository.saveUser(existingUser);
            }).orElseThrow(() -> new IllegalStateException("Cannot update the password because the user was not found."));
  }

  @Override
  public Address updateUserAddress(UUID userId, UpdateAddressRequestDTO updatedAddressRequest) {
    return this.userRepository.findUserById(userId)
            .map(existingUser -> {
              existingUser.getAddress().setStreet(updatedAddressRequest.street());
              existingUser.getAddress().setCity(updatedAddressRequest.city());
              existingUser.getAddress().setState(updatedAddressRequest.state());
              existingUser.getAddress().setZipCode(updatedAddressRequest.zipCode());
              existingUser.getAddress().setCountry(updatedAddressRequest.country());

              var updatedUser = this.userRepository.saveUser(existingUser);

              return updatedUser.getAddress();
            }).orElseThrow(() -> new IllegalStateException("Cannot update the address because the user was not found."));
  }

  @Override
  public void deleteUser(UUID userId) {
    this.userRepository.findUserById(userId)
            .map(userFound -> {
              this.userRepository.deleteUserById(userId);
              return userFound;
            });
  }

  @Override
  public List<User> findAllUsers() {
    return userRepository.findAllUsers();
  }
}
