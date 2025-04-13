package com.skilltrack.ai.service;

import com.skilltrack.ai.model.User;
import com.skilltrack.ai.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService( UserRepository userRepository ) {
		this.userRepository = userRepository;
	}

	public User getOrCreate( String username, String email ) {
		return userRepository.findByUsername( username ).orElseGet( () -> {
			User newUser = new User();
			newUser.setUsername( username );
			newUser.setEmail( email );
			return userRepository.save( newUser );
		} );
	}

	public User getOrUpdate( String username, String email ) {
		return userRepository.findByUsername( username ).map( existing -> {
			boolean updated = false;

			if ( email != null && !email.equals( existing.getEmail() ) ) {
				existing.setEmail( email );
				updated = true;
			}

			if ( updated ) {
				return userRepository.save( existing );
			}

			return existing;
		} ).orElseGet( () -> {
			User newUser = new User();
			newUser.setUsername( username );
			newUser.setEmail( email );
			return userRepository.save( newUser );
		} );
	}

}
