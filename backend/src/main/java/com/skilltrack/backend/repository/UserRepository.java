package com.skilltrack.backend.repository;

import com.skilltrack.backend.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findByUsername( String username );

	@Modifying
	@Transactional
	// language=PostgreSQL
	@Query( value = """
			INSERT INTO app_user (id, username, email)
			VALUES (:id, :username, :email)
			ON CONFLICT (username) DO NOTHING
			""", nativeQuery = true )
	void insertIfNotExists(
			@Param( "id" ) UUID id,
			@Param( "username" ) String username,
			@Param( "email" ) String email
	);
}
