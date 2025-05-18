package com.skilltrack.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response DTO for API errors
 */
@Builder
@JsonInclude( JsonInclude.Include.NON_NULL )
public record ErrorResponseDto(
		String message,
		int status,
		String error,
		String path,
		LocalDateTime timestamp,
		List<String> details
) {

	public ErrorResponseDto( String message, int status, String error, String path ) {
		this( message, status, error, path, LocalDateTime.now(), null );
	}
}