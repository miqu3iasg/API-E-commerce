package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.adapters.inbound.dtos.CreateUserRequestDTO;
import com.application.API_E_commerce.adapters.inbound.dtos.UpdateAddressRequestDTO;
import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.address.repository.AddressRepositoryPort;
import com.application.API_E_commerce.domain.address.useCase.AddressUseCase;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.repository.UserRepositoryPort;
import com.application.API_E_commerce.domain.user.services.UserService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepositoryPort userRepositoryPort;

	@Mock
	private AddressUseCase addressService;

	@Mock
	private AddressRepositoryPort addressRepositoryPort;

	@Nested
	class CreateUser {

		@Test
		@DisplayName("Should create a user successfully when no conflicting user exists")
		void shouldCreateUserSuccessfullyWhenNoConflictingUserExists () {
			CreateUserRequestDTO request = CreateUserRequestFactory.build();

			Address mockAddress = new Address();
			mockAddress.setStreet(request.address().street());
			mockAddress.setCity(request.address().city());
			mockAddress.setState(request.address().state());
			mockAddress.setZipCode(request.address().zipCode());
			mockAddress.setCountry(request.address().country());

			when(userRepositoryPort.saveUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0, User.class));

			User createdUser = userService.createUser(request);

			verify(userRepositoryPort).saveUser(any(User.class));

			assertNotNull(createdUser);
			assertEquals(request.name(), createdUser.getName());
			assertEquals(request.email(), createdUser.getEmail());
			assertEquals(request.password(), createdUser.getPassword());
			assertEquals(request.role(), createdUser.getRole());

			assertNotNull(createdUser.getAddress());
			assertEquals(request.address().street(), createdUser.getAddress().getStreet());
			assertEquals(request.address().city(), createdUser.getAddress().getCity());
			assertEquals(request.address().state(), createdUser.getAddress().getState());
			assertEquals(request.address().zipCode(), createdUser.getAddress().getZipCode());
			assertEquals(request.address().country(), createdUser.getAddress().getCountry());
			assertNotNull(createdUser.getCreatedAt());
		}

	}

	@Nested
	class UpdateUser {

		@Test
		@DisplayName("Should update the user name successfully when the user exists")
		void shouldUpdateUserNameSuccessfullyWhenUserExists () {
			final String updatedName = "Updated Name";
			final String oldName = "Old Name";

			User user = UserFactory.build();
			user.setName(oldName);

			when(userRepositoryPort.findUserById(user.getId())).thenReturn(Optional.of(user));

			userService.updateUserName(user.getId(), updatedName);

			verify(userRepositoryPort).saveUser(argThat(updatedUser -> updatedUser.getName().equals(updatedName)));
		}

		@Test
		@DisplayName("Should update the user password successfully when the user exists")
		void shouldUpdateUserPasswordSuccessfullyWhenUserExists () {
			final String updatedPassword = "newPassword123";
			final String oldPassword = "oldPassword123";

			User user = UserFactory.build();
			user.setPassword(oldPassword);

			when(userRepositoryPort.findUserById(user.getId())).thenReturn(Optional.of(user));

			userService.updatedUserPassword(user.getId(), updatedPassword);

			verify(userRepositoryPort).saveUser(argThat(updatedUser -> updatedUser.getPassword().equals(updatedPassword)));
		}

		@Test
		@DisplayName("Should update the user address successfully when the user exists")
		void shouldUpdateUserAddressSuccessfullyWhenUserExists () {
			UpdateAddressRequestDTO updateAddressRequest = UpdateAddressRequestFactory.build();

			User user = UserFactory.build();

			when(userRepositoryPort.findUserById(user.getId())).thenReturn(Optional.of(user));

			Address updatedAddress = userService.updateUserAddress(user.getId(), updateAddressRequest);

			assertEquals(updateAddressRequest.street(), updatedAddress.getStreet());
			assertEquals(updateAddressRequest.city(), updatedAddress.getCity());
			assertEquals(updateAddressRequest.state(), updatedAddress.getState());
			assertEquals(updateAddressRequest.zipCode(), updatedAddress.getZipCode());
			assertEquals(updateAddressRequest.country(), updatedAddress.getCountry());

			verify(userRepositoryPort).saveUser(user);
		}

	}

}