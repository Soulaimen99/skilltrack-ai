package com.skilltrack.ai.service;

import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

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
}
