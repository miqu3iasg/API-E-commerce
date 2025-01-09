package com.application.API_E_commerce.adapters.outbound.repositories;

import com.application.API_E_commerce.adapters.outbound.entities.user.JpaUserEntity;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.UserRepository;
import com.application.API_E_commerce.utils.converters.UserConverter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryImplementation implements UserRepository {

  private final JpaUserRepository jpaUserRepository;
  private final UserConverter userConverter;

  public UserRepositoryImplementation(JpaUserRepository jpaUserRepository, UserConverter userConverter) {
    this.jpaUserRepository = jpaUserRepository;
    this.userConverter = userConverter;
  }

  @Override
  public User saveUser(User user) {
    JpaUserEntity userEntityToSave = userConverter.toJpa(user);

    JpaUserEntity savedUserEntity = this.jpaUserRepository.save(userEntityToSave);

    return userConverter.toDomain(savedUserEntity);
  }

  @Override
  public Optional<User> findUserById(UUID userId) {
    return Optional.ofNullable(jpaUserRepository.findById(userId)
            .map(userConverter::toDomain)
            .orElseThrow(() -> new IllegalArgumentException("User was not found when searching for id in the repository.")));
  }

  @Override
  public Optional<User> findUserByName(String username) {
    return Optional.ofNullable(jpaUserRepository.findUserByName(username)
            .map(userConverter::toDomain)
            .orElseThrow(() -> new IllegalArgumentException("User was not found when searching for name in the repository.")));
  }

  @Override
  public List<User> findAllUsers() {
    List<JpaUserEntity> jpaUserEntityList = this.jpaUserRepository.findAll();

    if (jpaUserEntityList.isEmpty()) return Collections.emptyList();

    return jpaUserEntityList
            .stream()
            .map(userConverter::toDomain)
            .toList();
  }

  @Override
  public void deleteUser(User user) {
    JpaUserEntity userEntityToDelete = this.userConverter.toJpa(user);

    this.jpaUserRepository.findById(userEntityToDelete.getId())
            .map(existingUserEntity -> {
              this.jpaUserRepository.delete(existingUserEntity);
              return existingUserEntity;
            }).orElseThrow(() -> new IllegalArgumentException("User was not found when searching for id in the deleteUser method."));

  }

  @Override
  public void deleteUserById(UUID userId) {
    this.jpaUserRepository.findById(userId)
            .map(userEntity -> {
              this.jpaUserRepository.deleteById(userId);
              return userEntity;
            }).orElseThrow(() -> new IllegalArgumentException("User was not found when searching for id in the deleteById method."));
  }

  @Override
  public Optional<User> findUserByEmail(String email) {
    return Optional.ofNullable(this.jpaUserRepository.findUserByEmail(email)
            .map(userConverter::toDomain)
            .orElseThrow(() -> new IllegalArgumentException("User was not found when searching for email in the repository.")));
  }
}
