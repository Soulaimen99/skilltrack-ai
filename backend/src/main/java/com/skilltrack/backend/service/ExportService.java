package com.skilltrack.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skilltrack.backend.dto.LearningLogDto;
import com.skilltrack.backend.dto.SummaryDto;
import com.skilltrack.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ExportService {

	private static final Logger logger = LoggerFactory.getLogger( ExportService.class );

	private final LearningLogService learningLogService;

	private final SummaryService summaryService;

	public String exportLogs( User user, String format ) {
		logger.info( "Exporting logs for user: {} in format: {}", user.getId(), format );
		List<LearningLogDto> logs = learningLogService.getAllLogs( user );
		String result = switch ( format ) {
			case "json" -> exportAsJson( logs );
			case "txt" -> exportAsTxt( logs );
			default -> throw new IllegalArgumentException( "Unsupported format" );
		};
		logger.info( "Successfully exported {} logs for user: {}", logs.size(), user.getId() );
		return result;
	}

	public String exportSummaries( User user, String format ) {
		logger.info( "Exporting summaries for user: {} in format: {}", user.getId(), format );
		List<SummaryDto> summaries = summaryService.getAllSummaries( user );
		String result = switch ( format ) {
			case "json" -> exportAsJson( summaries );
			case "txt" -> exportAsTxt( summaries );
			default -> throw new IllegalArgumentException( "Unsupported format" );
		};
		logger.info( "Successfully exported {} summaries for user: {}", summaries.size(), user.getId() );
		return result;
	}

	private String exportAsJson( Object data ) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule( new JavaTimeModule() );
			mapper.disable( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS );
			String result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString( data );
			logger.debug( "Successfully exported data as JSON" );
			return result;
		}
		catch ( JsonProcessingException e ) {
			throw new RuntimeException( "Failed to export as JSON", e );
		}
	}

	private String exportAsTxt( List<?> items ) {
		String result = items.stream().map( Object::toString ).collect( Collectors.joining( "\n\n" ) );
		logger.debug( "Successfully exported data as TXT" );
		return result;
	}
}
