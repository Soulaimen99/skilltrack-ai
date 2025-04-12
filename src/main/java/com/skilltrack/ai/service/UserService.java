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

	public User getOrCreate( String username ) {
		return userRepository.findByUsername( username ).orElseGet( () -> {
			User newUser = new User();
			newUser.setUsername( username );
			return userRepository.save( newUser );
		} );
	}
}
