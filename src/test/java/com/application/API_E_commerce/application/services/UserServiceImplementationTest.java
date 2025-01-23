package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.address.dtos.UpdateAddressRequestDTO;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.repository.UserRepository;
import com.application.API_E_commerce.domain.user.dtos.CreateUserRequestDTO;
import com.application.API_E_commerce.factory.CreateUserRequestFactory;
import com.application.API_E_commerce.factory.UpdateAddressRequestFactory;
import com.application.API_E_commerce.factory.UserFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplementationTest {

  @InjectMocks
  private UserServiceImplementation userService;

  @Mock
  private UserRepository userRepository;

  @Nested
  class CreateUser {

    @Test
    @DisplayName("Should create a user successfully when no conflicting user exists")
    void shouldCreateUserSuccessfullyWhenNoConflictingUserExists() {
      CreateUserRequestDTO request = CreateUserRequestFactory.build();

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
      CreateUserRequestDTO request = CreateUserRequestFactory.build();

      when(userRepository.findUserByName(request.name())).thenReturn(Optional.of(new User()));

      Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
      assertEquals("A user with this name already exists.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when creating a user with a duplicate email")
    void shouldThrowExceptionWhenCreatingUserWithDuplicateEmail() {
      CreateUserRequestDTO request = CreateUserRequestFactory.build();

      when(userRepository.findUserByName(request.name())).thenReturn(Optional.empty());
      when(userRepository.findUserByEmail(request.email())).thenReturn(Optional.of(new User()));

      Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
      assertEquals("A user with this email already exists.", exception.getMessage());
    }
  }

  @Nested
  class UpdateUser {

    @Test
    @DisplayName("Should update the user name successfully when the user exists")
    void shouldUpdateUserNameSuccessfullyWhenUserExists() {
      String updatedName = "Updated Name";
      String oldName = "Old Name";

      User user = UserFactory.build();
      user.setName(oldName);

      when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));

      userService.updateUserName(user.getId(), updatedName);

      verify(userRepository).saveUser(argThat(updatedUser -> updatedUser.getName().equals(updatedName)));
    }

    @Test
    @DisplayName("Should update the user password successfully when the user exists")
    void shouldUpdateUserPasswordSuccessfullyWhenUserExists() {
      String updatedPassword = "newPassword123";
      String oldPassword = "oldPassword123";

      User user = UserFactory.build();
      user.setPassword(oldPassword);

      when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));

      userService.updatedUserPassword(user.getId(), updatedPassword);

      verify(userRepository).saveUser(argThat(updatedUser -> updatedUser.getPassword().equals(updatedPassword)));
    }

    @Test
    @DisplayName("Should update the user address successfully when the user exists")
    void shouldUpdateUserAddressSuccessfullyWhenUserExists() {
      UpdateAddressRequestDTO updateAddressRequest = UpdateAddressRequestFactory.build();

      User user = UserFactory.build();

      when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));

      Address updatedAddress = userService.updateUserAddress(user.getId(), updateAddressRequest);

      assertEquals(updateAddressRequest.street(), updatedAddress.getStreet());
      assertEquals(updateAddressRequest.city(), updatedAddress.getCity());
      assertEquals(updateAddressRequest.state(), updatedAddress.getState());
      assertEquals(updateAddressRequest.zipCode(), updatedAddress.getZipCode());
      assertEquals(updateAddressRequest.country(), updatedAddress.getCountry());

      verify(userRepository).saveUser(user);
    }
  }
}