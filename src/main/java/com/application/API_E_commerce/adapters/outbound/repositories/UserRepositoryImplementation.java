package com.application.API_E_commerce.adapters.outbound.repositories;

import com.application.API_E_commerce.adapters.outbound.entities.user.JpaUserEntity;
import com.application.API_E_commerce.adapters.outbound.repositories.jpa.JpaUserRepository;
import com.application.API_E_commerce.common.utils.mappers.UserMapper;
import com.application.API_E_commerce.domain.user.User;
import com.application.API_E_commerce.domain.user.repository.UserRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryImplementation implements UserRepositoryPort {

	private final JpaUserRepository jpaUserRepository;
	private final UserMapper userMapper;

	public UserRepositoryImplementation (JpaUserRepository jpaUserRepository, UserMapper userMapper) {
		this.jpaUserRepository = jpaUserRepository;
		this.userMapper = userMapper;
	}

	@Override
	@Transactional
	public User saveUser (User user) {
		JpaUserEntity userEntityToSave = userMapper.toJpa(user);

		JpaUserEntity savedUserEntity = jpaUserRepository.save(userEntityToSave);

		return userMapper.toDomain(savedUserEntity);
	}

	@Override
	public Optional<User> findUserById (UUID userId) {
		return Optional.ofNullable(jpaUserRepository.findById(userId)
				.map(userMapper::toDomain)
				.orElseThrow(() -> new IllegalArgumentException("User was not found when searching for id in the repository.")));
	}

	@Override
	public Optional<User> findUserByName (String username) {
		return Optional.ofNullable(jpaUserRepository.findUserByName(username)
				.map(userMapper::toDomain)
				.orElseThrow(() -> new IllegalArgumentException("User was not found when searching for name in the repository.")));
	}

	@Override
	public List<User> findAllUsers () {
		List<JpaUserEntity> jpaUserEntityList = jpaUserRepository.findAll();

		if (jpaUserEntityList.isEmpty()) return Collections.emptyList();

		return jpaUserEntityList.stream().map(userMapper::toDomain).toList();
	}

	@Override
	public void deleteUser (User user) {
		JpaUserEntity userEntityToDelete = userMapper.toJpa(user);

		jpaUserRepository.findById(userEntityToDelete.getId())
				.map(existingUserEntity -> {
					jpaUserRepository.delete(existingUserEntity);
					return existingUserEntity;
				}).orElseThrow(() -> new IllegalArgumentException("User was not found when searching for id in the deleteUser method."));

	}

	@Override
	public void deleteUserById (UUID userId) {
		jpaUserRepository.findById(userId)
				.map(existingUserEntity -> {
					jpaUserRepository.deleteById(userId);
					return existingUserEntity;
				}).orElseThrow(() -> new IllegalArgumentException("User was not found when searching for id in the deleteById method."));
	}

	@Override
	public Optional<User> findUserByEmail (String email) {
		return Optional.ofNullable(jpaUserRepository.findUserByEmail(email)
				.map(userMapper::toDomain)
				.orElseThrow(() -> new IllegalArgumentException("User was not found when searching for email in the repository.")));
	}

}
