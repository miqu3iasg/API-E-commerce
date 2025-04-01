package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.UserUseCases;
import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.address.dtos.UpdateAddressRequestDTO;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.UserRole;
import com.application.API_E_commerce.domain.user.dtos.CreateUserRequestDTO;
import com.application.API_E_commerce.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImplementation implements UserUseCases {

  private final UserRepository userRepository;

  public UserServiceImplementation ( UserRepository userRepository ) {
    this.userRepository = userRepository;
  }

  @Override
  public User createUser ( CreateUserRequestDTO createUserRequest ) {
    validateUniqueEmail(createUserRequest.email());
    validateUserPassword(createUserRequest.password());
    validateUserAddress(createUserRequest.address());
    validateUserRole(createUserRequest.role().name());

    User user = new User();
    user.setName(createUserRequest.name());
    user.setEmail(createUserRequest.email());
    user.setPassword(createUserRequest.password());
    user.setRole(createUserRequest.role());

    Address address = new Address();
    address.setCity(createUserRequest.address().getCity());
    address.setCountry(createUserRequest.address().getCountry());
    address.setState(createUserRequest.address().getState());
    address.setStreet(createUserRequest.address().getStreet());
    address.setZipCode(createUserRequest.address().getZipCode());
    user.setAddress(address);
    user.setCreatedAt(LocalDateTime.now());

    return this.userRepository.saveUser(user);
  }

  private void validateUniqueEmail ( String email ) {
    if ( this.userRepository.findUserByEmail(email).isPresent() ) {
      throw new IllegalArgumentException("A user with this email already exists.");
    }
  }

  private void validateUserPassword ( String password ) {
    if ( password.length() < 8 ) {
      throw new IllegalArgumentException("Password must be at least 8 characters long.");
    }

    if ( password.trim().isEmpty() ) {
      throw new IllegalArgumentException("Password cannot be empty.");
    }
  }

  private void validateUserAddress ( Address address ) {
    if ( address == null ) {
      throw new IllegalArgumentException("Address cannot be null.");
    }
    if ( address.getCountry() == null || address.getCountry().trim().isEmpty() ) {
      throw new IllegalArgumentException("Country cannot be empty.");
    }
    if ( address.getState() == null || address.getState().trim().isEmpty() ) {
      throw new IllegalArgumentException("State cannot be empty.");
    }
    if ( address.getCity() == null || address.getCity().trim().isEmpty() ) {
      throw new IllegalArgumentException("City cannot be empty.");
    }
    if ( address.getStreet() == null || address.getStreet().trim().isEmpty() ) {
      throw new IllegalArgumentException("Street cannot be empty.");
    }
    if ( address.getZipCode() == null || address.getZipCode().trim().isEmpty() ) {
      throw new IllegalArgumentException("Zip code cannot be empty.");
    }
  }

  private void validateUserRole ( String role ) {
    if ( role == null || role.trim().isEmpty() ) {
      throw new IllegalArgumentException("Role cannot be empty.");
    }
    try {
      UserRole.valueOf(role.toUpperCase());
    } catch ( IllegalArgumentException e ) {
      throw new IllegalArgumentException("Invalid role.");
    }
  }

  @Override
  public Optional<User> findUserById ( UUID userId ) {
    return userRepository.findUserById(userId);
  }

  @Override
  public User updateUserName ( UUID userId, String updatedNameRequest ) {
    return this.userRepository.findUserById(userId)
            .map(existingUser -> {
              existingUser.setName(updatedNameRequest);
              this.userRepository.saveUser(existingUser);
              return existingUser;
            }).orElseThrow(() -> new IllegalArgumentException("Cannot update the name because the user was not found."));
  }

  @Override
  public void updatedUserPassword ( UUID userId, String updatedPasswordRequest ) {
    this.userRepository.findUserById(userId)
            .map(existingUser -> {
              existingUser.setPassword(updatedPasswordRequest);
              this.userRepository.saveUser(existingUser);
              return existingUser;
            }).orElseThrow(() -> new IllegalArgumentException("Cannot update the password because the user was not found."));
  }

  @Override
  public Address updateUserAddress ( UUID userId, UpdateAddressRequestDTO updatedAddressRequest ) {
    return this.userRepository.findUserById(userId)
            .map(existingUser -> {
              existingUser.getAddress().setStreet(updatedAddressRequest.street());
              existingUser.getAddress().setCity(updatedAddressRequest.city());
              existingUser.getAddress().setState(updatedAddressRequest.state());
              existingUser.getAddress().setZipCode(updatedAddressRequest.zipCode());
              existingUser.getAddress().setCountry(updatedAddressRequest.country());

              this.userRepository.saveUser(existingUser);

              return existingUser.getAddress();
            }).orElseThrow(() -> new IllegalArgumentException("Cannot update the address because the user was not found."));
  }

  @Override
  public void deleteUser ( UUID userId ) {
    this.userRepository.findUserById(userId)
            .map(userFound -> {
              this.userRepository.deleteUserById(userId);
              return userFound;
            });
  }

  @Override
  public List<User> findAllUsers () {
    return userRepository.findAllUsers();
  }

}
