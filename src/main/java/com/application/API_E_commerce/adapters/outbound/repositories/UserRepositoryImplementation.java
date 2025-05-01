package com.application.API_E_commerce.adapters.outbound.repositories;

import com.application.API_E_commerce.adapters.outbound.entities.user.JpaUserEntity;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.repository.UserRepository;
import com.application.API_E_commerce.utils.mappers.UserMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryImplementation implements UserRepository {

  private final JpaUserRepository jpaUserRepository;
  private final UserMapper userMapper;

  public UserRepositoryImplementation ( JpaUserRepository jpaUserRepository, UserMapper userMapper ) {
    this.jpaUserRepository = jpaUserRepository;
    this.userMapper = userMapper;
  }

  @Override
  @Transactional
  public User saveUser ( User user ) {
    JpaUserEntity userEntityToSave = userMapper.toJpa(user);

    JpaUserEntity savedUserEntity = this.jpaUserRepository.save(userEntityToSave);

    return userMapper.toDomain(savedUserEntity);
  }

  @Override
  public Optional<User> findUserById ( UUID userId ) {
    return Optional.ofNullable(jpaUserRepository.findById(userId)
            .map(userMapper::toDomain)
            .orElseThrow(() -> new IllegalArgumentException("User was not found when searching for id in the repository.")));
  }

  @Override
  public Optional<User> findUserByName ( String username ) {
    return Optional.ofNullable(jpaUserRepository.findUserByName(username)
            .map(userMapper::toDomain)
            .orElseThrow(() -> new IllegalArgumentException("User was not found when searching for name in the repository.")));
  }

  @Override
  public List<User> findAllUsers () {
    List<JpaUserEntity> jpaUserEntityList = this.jpaUserRepository.findAll();

    if ( jpaUserEntityList.isEmpty() ) return Collections.emptyList();

    return jpaUserEntityList.stream().map(userMapper::toDomain).toList();
  }

  @Override
  public void deleteUser ( User user ) {
    JpaUserEntity userEntityToDelete = this.userMapper.toJpa(user);

    this.jpaUserRepository.findById(userEntityToDelete.getId())
            .map(existingUserEntity -> {
              this.jpaUserRepository.delete(existingUserEntity);
              return existingUserEntity;
            }).orElseThrow(() -> new IllegalArgumentException("User was not found when searching for id in the deleteUser method."));

  }

  @Override
  public void deleteUserById ( UUID userId ) {
    this.jpaUserRepository.findById(userId)
            .map(existingUserEntity -> {
              this.jpaUserRepository.deleteById(userId);
              return existingUserEntity;
            }).orElseThrow(() -> new IllegalArgumentException("User was not found when searching for id in the deleteById method."));
  }

  @Override
  public Optional<User> findUserByEmail ( String email ) {
    return Optional.ofNullable(this.jpaUserRepository.findUserByEmail(email)
            .map(userMapper::toDomain)
            .orElseThrow(() -> new IllegalArgumentException("User was not found when searching for email in the repository.")));
  }

}
