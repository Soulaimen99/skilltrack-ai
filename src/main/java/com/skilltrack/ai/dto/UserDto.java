package com.skilltrack.ai.dto;

import com.skilltrack.ai.entity.User;

import java.time.LocalDate;
import java.util.UUID;

public record UserDto( UUID id, String username, String email, int remainingSummaries, LocalDate lastLogDate ) {

	public static UserDto from( User user, int remainingSummaries, LocalDate lastLogDate ) {
		return new UserDto( user.getId(), user.getUsername(), user.getEmail(), remainingSummaries, lastLogDate );
	}
}
