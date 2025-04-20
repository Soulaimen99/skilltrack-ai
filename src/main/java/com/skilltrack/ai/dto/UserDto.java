package com.skilltrack.ai.dto;

import com.skilltrack.ai.entity.User;

public record UserDto( String username, String email ) {

	public static UserDto from( User user ) {
		return new UserDto( user.getUsername(), user.getEmail() );
	}
}
