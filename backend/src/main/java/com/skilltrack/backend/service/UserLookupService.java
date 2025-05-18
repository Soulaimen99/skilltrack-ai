package com.skilltrack.backend.service;

import com.skilltrack.backend.entity.User;
import com.skilltrack.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserLookupService {

	private final UserRepository userRepository;

	@Transactional
	public User getById( UUID id ) {
		return userRepository.findById( id )
				.orElseThrow( () -> new IllegalStateException( "User not found" ) );
	}

	@Transactional
	public User get( String username, String email ) {
		return userRepository.findByUsername( username )
				.map( u -> updateEmailIfChanged( u, email ) )
				.orElseThrow( () -> new IllegalStateException( "User not found" ) );
	}

	@Transactional
	public User getOrCreate( String username, String email ) {
		return userRepository.findByUsername( username )
				.map( u -> updateEmailIfChanged( u, email ) )
				.orElseGet( () -> {
					UUID id = UUID.randomUUID();
					userRepository.insertIfNotExists( id, username, email );
					return userRepository.findByUsername( username )
							.orElseThrow( () -> new IllegalStateException( "User insert failed unexpectedly" ) );
				} );
	}

	private User updateEmailIfChanged( User u, String email ) {
		if ( email != null && !email.equals( u.getEmail() ) ) {
			u.setEmail( email );
		}
		return u;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
}
