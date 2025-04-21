package com.skilltrack.ai.dto;

import com.skilltrack.ai.entity.User;

import java.util.UUID;

public record UserDto( UUID id, String username, String email ) {

	public static UserDto from( User user ) {
		return new UserDto( user.getId(), user.getUsername(), user.getEmail() );
	}
}
