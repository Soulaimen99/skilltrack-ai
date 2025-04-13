package com.skilltrack.ai.repository;

import com.skilltrack.ai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findByUsername( String username );
}

