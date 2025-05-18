package com.skilltrack.backend.controller;

import com.skilltrack.backend.dto.LearningLogDto;
import com.skilltrack.backend.dto.SummaryDto;
import com.skilltrack.backend.entity.User;
import com.skilltrack.backend.service.ExportService;
import com.skilltrack.backend.service.SummaryService;
import com.skilltrack.backend.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping( "/api/summaries" )
@SecurityRequirement( name = "bearerAuth" )
@PreAuthorize( "hasRole( 'user' )" )
public class SummaryController {

	private final UserService userService;

	private final SummaryService summaryService;

	private final ExportService exportService;

	public SummaryController( UserService userService, SummaryService summaryService, ExportService exportService ) {
		this.userService = userService;
		this.summaryService = summaryService;
		this.exportService = exportService;
	}

	@PostMapping
	public ResponseEntity<SummaryDto> summarize( @RequestBody List<LearningLogDto> logs, Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		SummaryDto summaryDto = summaryService.summarizeWithLimitCheck( user, logs );
		int remaining = summaryService.remaining( user );

		return ResponseEntity.ok().header( "X-RateLimit-Remaining", String.valueOf( remaining ) ).body( summaryDto );
	}

	@GetMapping( "/export" )
	public ResponseEntity<byte[]> exportSummaries( @RequestParam( defaultValue = "json" ) String format, Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		String content = exportService.exportSummaries( user, format );
		String contentType = format.equalsIgnoreCase( "txt" ) ? "text/plain" : "application/json";
		String filename = "summaries." + format;

		return ResponseEntity.ok()
				.header( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"" )
				.header( HttpHeaders.CONTENT_TYPE, contentType )
				.body( content.getBytes( StandardCharsets.UTF_8 ) );
	}
}
