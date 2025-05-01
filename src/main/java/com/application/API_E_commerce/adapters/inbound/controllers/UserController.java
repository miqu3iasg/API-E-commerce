package com.application.API_E_commerce.adapters.inbound.controllers;

import com.application.API_E_commerce.application.usecases.UserUseCases;
import com.application.API_E_commerce.domain.address.Address;
import com.application.API_E_commerce.domain.address.dtos.UpdateAddressRequestDTO;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.dtos.CreateUserRequestDTO;
import com.application.API_E_commerce.domain.user.dtos.UpdateUserNameRequestDTO;
import com.application.API_E_commerce.domain.user.dtos.UpdateUserPasswordRequestDTO;
import com.application.API_E_commerce.domain.user.dtos.UserResponseDTO;
import com.application.API_E_commerce.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserUseCases userService;

  public UserController ( UserUseCases userService ) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UserResponseDTO>> createUser ( @RequestBody CreateUserRequestDTO request ) {
    User createdUser = userService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("User created successfully", new UserResponseDTO(createdUser), HttpStatus.CREATED));
  }

  @PutMapping("/{userId}/name")
  public ResponseEntity<ApiResponse<UserResponseDTO>> updateUserName ( @PathVariable UUID userId, @RequestBody UpdateUserNameRequestDTO request ) {
    User updatedUser = userService.updateUserName(userId, request.name());
    return ResponseEntity.ok(ApiResponse.success("User name updated successfully", new UserResponseDTO(updatedUser), HttpStatus.OK));
  }

  @PutMapping("/{userId}/password")
  public ResponseEntity<Void> updateUserPassword ( @PathVariable UUID userId, @RequestBody UpdateUserPasswordRequestDTO request ) {
    userService.updatedUserPassword(userId, request.password());
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{userId}/address")
  public ResponseEntity<ApiResponse<Address>> updateUserAddress ( @PathVariable UUID userId, @RequestBody UpdateAddressRequestDTO request ) {
    Address updatedAddress = userService.updateUserAddress(userId, request);

    return ResponseEntity.ok(ApiResponse.success("User address updated successfully", updatedAddress, HttpStatus.OK));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<UserResponseDTO>>> findAllUsers () {
    List<UserResponseDTO> users = userService.findAllUsers()
            .stream()
            .map(UserResponseDTO::new)
            .toList();

    return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users, HttpStatus.OK));
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser ( @PathVariable UUID userId ) {
    userService.deleteUser(userId);

    return ResponseEntity.noContent().build();
  }

}
