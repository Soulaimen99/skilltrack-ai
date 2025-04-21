package com.skilltrack.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skilltrack.ai.dto.LearningLogDto;
import com.skilltrack.ai.dto.SummaryDto;
import com.skilltrack.ai.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ExportService {

	private final LearningLogService learningLogService;

	private final SummaryService summaryService;

	public ExportService( LearningLogService learningLogService, SummaryService summaryService ) {
		this.learningLogService = learningLogService;
		this.summaryService = summaryService;
	}

	public String exportLogs( User user, String format ) {
		List<LearningLogDto> logs = learningLogService.getAllLogs( user );
		return switch ( format ) {
			case "json" -> exportAsJson( logs );
			case "txt" -> exportAsTxt( logs );
			default -> throw new IllegalArgumentException( "Unsupported format" );
		};
	}

	public String exportSummaries( User user, String format ) {
		List<SummaryDto> summaries = summaryService.getAllSummaries( user );
		return switch ( format ) {
			case "json" -> exportAsJson( summaries );
			case "txt" -> exportAsTxt( summaries );
			default -> throw new IllegalArgumentException( "Unsupported format" );
		};
	}

	private String exportAsJson( Object data ) {
		try {
			return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString( data );
		}
		catch ( JsonProcessingException e ) {
			throw new RuntimeException( "Failed to export as JSON", e );
		}
	}

	private String exportAsTxt( List<?> items ) {
		return items.stream().map( Object::toString ).collect( Collectors.joining( "\n\n" ) );
	}
}
