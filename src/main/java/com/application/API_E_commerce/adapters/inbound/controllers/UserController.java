package com.application.API_E_commerce.adapters.inbound.controllers;

import com.application.API_E_commerce.adapters.inbound.dtos.*;
import com.application.API_E_commerce.common.response.ApiResponse;
import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.useCase.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "Operations pertaining to user management")
public class UserController {

	private final UserUseCase userService;

	public UserController (UserUseCase userService) {
		this.userService = userService;
	}

	@Operation(
			summary = "Creates a new user",
			description = "Creates a user account with the provided data"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "201",
					description = "User created successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid user request data",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@PostMapping
	public ResponseEntity<ApiResponse<UserResponseDTO>> createUser (
			@Parameter(description = "User data request to create", required = true)
			@Valid @RequestBody CreateUserRequestDTO request
	) {
		User createdUser = userService.createUser(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("User created successfully", new UserResponseDTO(createdUser), HttpStatus.CREATED));
	}

	@Operation(
			summary = "Update user name",
			description = "Updates the user's name using the provided user ID",
			tags = {"User update"}
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "The user name was updated successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid request data",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "User not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@PutMapping("/{userId}/name")
	public ResponseEntity<ApiResponse<UserResponseDTO>> updateUserName (
			@Parameter(description = "User ID to update", required = true)
			@PathVariable UUID userId,
			@Parameter(description = "User name request to update", required = true)
			@Valid @RequestBody UpdateUserNameRequestDTO request
	) {
		User updatedUser = userService.updateUserName(userId, request.name());
		return ResponseEntity.ok(ApiResponse.success("User name updated successfully", new UserResponseDTO(updatedUser), HttpStatus.OK));
	}

	@Operation(
			summary = "Update user password",
			description = "Updates the user's password using the provided user ID",
			tags = {"User update"}
	)

	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "204",
					description = "Password updated successfully (no content)"
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid password format or data",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "User not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@PutMapping("/{userId}/password")
	public ResponseEntity<Void> updateUserPassword (
			@Parameter(description = "User ID to update", required = true)
			@PathVariable UUID userId,
			@Parameter(description = "User password request to update", required = true)
			@Valid @RequestBody UpdateUserPasswordRequestDTO request
	) {
		userService.updatedUserPassword(userId, request.password());
		return ResponseEntity.noContent().build();
	}

	@Operation(
			summary = "Update user address",
			description = "Updates the address information of the specified user",
			tags = {"User update"}
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "User address updated successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid address data",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "User not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@PutMapping("/{userId}/address")
	public ResponseEntity<ApiResponse<Address>> updateUserAddress (
			@Parameter(description = "User ID to update", required = true)
			@PathVariable UUID userId,
			@Parameter(description = "User address request to update", required = true)
			@Valid @RequestBody UpdateAddressRequestDTO request
	) {
		Address updatedAddress = userService.updateUserAddress(userId, request);

		return ResponseEntity.ok(ApiResponse.success("User address updated successfully", updatedAddress, HttpStatus.OK));
	}

	@Operation(
			summary = "Retrive all users",
			description = "Returns a list os all registeres users"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Users retrieved successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class),
							array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class))
					)
			)
	})
	@GetMapping
	public ResponseEntity<ApiResponse<List<UserResponseDTO>>> findAllUsers () {
		List<UserResponseDTO> users = userService.findAllUsers()
				.stream()
				.map(UserResponseDTO::new)
				.toList();

		return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users, HttpStatus.OK));
	}

	@Operation(
			summary = "Delete user",
			description = "Deletes a user account using the provided user ID"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "204",
					description = "User deleted successfully (no content)"
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "User not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUser (
			@Parameter(description = "User ID to delete", required = true)
			@PathVariable UUID userId
	) {
		userService.deleteUser(userId);

		return ResponseEntity.noContent().build();
	}

}
