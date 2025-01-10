package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.address.dtos.UpdateAddressRequestDTO;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.UserRepository;
import com.application.API_E_commerce.domain.user.UserRole;
import com.application.API_E_commerce.domain.user.dtos.CreateUserRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplementationTest {

  @InjectMocks
  private UserServiceImplementation userService;

  @Mock
  private UserRepository userRepository;

  @Test
  @DisplayName("Should create a user successfully when no conflicting user exists")
  void shouldCreateUserSuccessfullyWhenNoConflictingUserExists() {
    Address address = new Address(UUID.randomUUID(), "Street 1", "City", "State", "12345", "Country");

    CreateUserRequestDTO request = new CreateUserRequestDTO(
            "John Doe",
            "john.doe@example.com",
            "password123",
            UserRole.CUSTOMER_ROLE,
            address
    );

    when(userRepository.findUserByName(request.name())).thenReturn(Optional.empty());
    when(userRepository.findUserByEmail(request.email())).thenReturn(Optional.empty());

    userService.createUser(request);

    verify(userRepository).saveUser(argThat(user ->
            user.getName().equals(request.name()) &&
            user.getEmail().equals(request.email()) &&
            user.getPassword().equals(request.password()) &&
            user.getRole().equals(request.role()) &&
            user.getAddress().equals(request.address()) &&
            user.getCreatedAt() != null
    ));
  }

  @Test
  @DisplayName("Should throw exception when creating a user with a duplicate name")
  void shouldThrowExceptionWhenCreatingUserWithDuplicateName() {
    Address address = new Address(UUID.randomUUID(), "Street 1", "City", "State", "12345", "Country");

    CreateUserRequestDTO request = new CreateUserRequestDTO(
            "John Doe",
            "john.doe@example.com",
            "password123",
            UserRole.CUSTOMER_ROLE,
            address
    );

    when(userRepository.findUserByName(request.name())).thenReturn(Optional.of(new User()));

    Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
    assertEquals("A user with this name already exists.", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw exception when creating a user with a duplicate email")
  void shouldThrowExceptionWhenCreatingUserWithDuplicateEmail() {
    Address address = new Address(UUID.randomUUID(), "Street 1", "City", "State", "12345", "Country");

    CreateUserRequestDTO request = new CreateUserRequestDTO(
            "John Doe",
            "john.doe@example.com",
            "password123",
            UserRole.CUSTOMER_ROLE,
            address
    );

    when(userRepository.findUserByName(request.name())).thenReturn(Optional.empty());
    when(userRepository.findUserByEmail(request.email())).thenReturn(Optional.of(new User()));

    Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
    assertEquals("A user with this email already exists.", exception.getMessage());
  }

  @Test
  @DisplayName("Should update the user name successfully when the user exists")
  void shouldUpdateUserNameSuccessfullyWhenUserExists() {
    UUID userId = UUID.randomUUID();
    String updatedName = "Updated Name";
    String oldName = "Old Name";

    User user = new User();
    user.setName(oldName);

    when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));

    userService.updateUserName(userId, updatedName);

    verify(userRepository).saveUser(argThat(updatedUser -> updatedUser.getName().equals(updatedName)));
  }

  @Test
  @DisplayName("Should update the user password successfully when the user exists")
  void shouldUpdateUserPasswordSuccessfullyWhenUserExists() {
    UUID userId = UUID.randomUUID();
    String updatedPassword = "newPassword123";
    String oldPassword = "oldPassword123";

    User user = new User();
    user.setPassword(oldPassword);

    when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));

    userService.updatedUserPassword(userId, updatedPassword);

    verify(userRepository).saveUser(argThat(updatedUser -> updatedUser.getPassword().equals(updatedPassword)));
  }

  @Test
  @DisplayName("Should update the user address successfully when the user exists")
  void shouldUpdateUserAddressSuccessfullyWhenUserExists() {
    UUID userId = UUID.randomUUID();

    UpdateAddressRequestDTO updateAddressRequest = new UpdateAddressRequestDTO(
            "New Street",
            "New City",
            "New State",
            "54321",
            "New Country"
    );

    User user = new User();
    user.setAddress(new Address(UUID.randomUUID(), "Old Street", "Old City", "Old State", "12345", "Old Country"));

    when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));

    Address updatedAddress = userService.updateUserAddress(userId, updateAddressRequest);

    assertEquals(updateAddressRequest.street(), updatedAddress.getStreet());
    assertEquals(updateAddressRequest.city(), updatedAddress.getCity());
    assertEquals(updateAddressRequest.state(), updatedAddress.getState());
    assertEquals(updateAddressRequest.zipCode(), updatedAddress.getZipCode());
    assertEquals(updateAddressRequest.country(), updatedAddress.getCountry());

    verify(userRepository).saveUser(user);
  }
}