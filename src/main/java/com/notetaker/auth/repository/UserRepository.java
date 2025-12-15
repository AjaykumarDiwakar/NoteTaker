package com.notetaker.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.notetaker.auth.entity.User;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, String> {
	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String username);

	@Transactional
	@Modifying
	@Query("update User u set u.password = ?2 where u.email = ?1")
	void updatePassword(String email, String password);
}
