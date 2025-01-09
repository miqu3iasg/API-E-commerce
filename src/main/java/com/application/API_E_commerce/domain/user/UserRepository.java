package com.application.API_E_commerce.domain.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
  User saveUser(User user);
  Optional<User> findUserById(UUID userId);
  Optional<User> findUserByName(String username);
  List<User> findAllUsers();
  void deleteUser(User user);
  void deleteUserById(UUID userId);
  Optional<User> findUserByEmail(String email);
}
