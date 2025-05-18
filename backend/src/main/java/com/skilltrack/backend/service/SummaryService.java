package com.skilltrack.backend.service;

import com.skilltrack.backend.dto.LearningLogDto;
import com.skilltrack.backend.dto.SummaryDto;
import com.skilltrack.backend.entity.Summary;
import com.skilltrack.backend.entity.User;
import com.skilltrack.backend.repository.SummaryRepository;
import com.skilltrack.backend.repository.SummaryUsageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class SummaryService {

	private static final Logger logger = LoggerFactory.getLogger( SummaryService.class );

	private final OpenAiChatModel chatModel;

	private final SummaryRepository summaryRepository;

	private final SummaryUsageRepository summaryUsageRepository;

	private final SummaryUsageService summaryUsageService;

	@Value( "${summary.limit.daily:10}" )
	private int dailyLimit;

	public SummaryService( OpenAiChatModel chatModel, SummaryRepository summaryRepository, SummaryUsageRepository summaryUsageRepository, SummaryUsageService summaryUsageService ) {
		this.chatModel = chatModel;
		this.summaryRepository = summaryRepository;
		this.summaryUsageRepository = summaryUsageRepository;
		this.summaryUsageService = summaryUsageService;
	}

	public List<SummaryDto> getAllSummaries( User user ) {
		List<Summary> summaries = summaryRepository.findByUser( user );
		logger.info( "Retrieved {} summaries for user: {}", summaries.size(), user.getId() );
		return summaries.stream().map( SummaryDto::from ).toList();
	}

	public SummaryDto.PagedSummariesResponse getPagedSummariesResponse( String from, String to, int page, Integer size, User user ) {
		logger.info( "Retrieving paged summaries with parameters: from={}, to={}, page={}, size={} for user: {}",
				from, to, page, size, user.getId() );

		LocalDateTime dtFrom = from != null ? LocalDate.parse( from ).atStartOfDay() : null;
		LocalDateTime dtTo = to != null ? LocalDate.parse( to ).atTime( LocalTime.MAX ) : null;

		Pageable pageable;
		if ( size == null ) {
			logger.debug( "Using unpaged pageable" );
			pageable = Pageable.unpaged();
		}
		else {
			logger.debug( "Using paged pageable with page: {} and size: {}", page, size );
			pageable = PageRequest.of( page, size, Sort.by( "createdAt" ).descending() );
		}

		Page<Summary> summaryPage = getSummaries( user, dtFrom, dtTo, pageable );
		List<SummaryDto> content = summaryPage.getContent().stream()
				.map( SummaryDto::from )
				.toList();

		logger.info( "Retrieved {} summaries (page {} of {}) for user: {}",
				summaryPage.getNumberOfElements(), summaryPage.getNumber() + 1, summaryPage.getTotalPages(), user.getId() );

		return new SummaryDto.PagedSummariesResponse( content, summaryPage.getNumber(), summaryPage.getSize(), summaryPage.getTotalPages(), summaryPage.getTotalElements() );
	}

	public Page<Summary> getSummaries( User user, LocalDateTime from, LocalDateTime to, Pageable pageable ) {
		if ( from != null && to != null ) {
			logger.debug( "Using date range filter for summaries" );
			return summaryRepository.findByUserAndCreatedAtBetween( user, from, to, pageable );
		}

		logger.debug( "Fetching summaries without date range filter" );
		return summaryRepository.findByUser( user, pageable );
	}

	public SummaryDto summarizeWithLimitCheck( User user, List<LearningLogDto> logs ) {
		logger.info( "Attempting to create summary for user: {} with {} logs", user.getId(), logs.size() );

		if ( !allow( user ) ) {
			logger.warn( "User: {} has exceeded daily summary limit", user.getId() );
			throw new ResponseStatusException( HttpStatus.TOO_MANY_REQUESTS );
		}

		List<String> contents = LearningLogDto.toContentList( logs );

		String summaryText = summarize( user.getUsername(), contents );

		Summary saved = summaryRepository.save( new Summary( null, user, summaryText, LocalDateTime.now() ) );
		logger.info( "Successfully created summary with ID: {} for user: {}", saved.getId(), user.getId() );

		return SummaryDto.from( saved );
	}

	private String summarize( String username, List<String> contents ) {
		String promptText = String.format( """
				You are an intelligent learning assistant for a user named %s.
				Summarize the following personal learning reflections in a friendly and motivational tone.
				Include a short "Next Step" section with 1 action the user should take next.
				
				Reflections:
				%s
				""", username, contents.stream().map( s -> "- " + s ).collect( Collectors.joining( "\n" ) ) );

		String result = chatModel.call( new Prompt( promptText ) ).getResult().getOutput().getText();
		logger.debug( "Successfully generated summary from AI model" );

		return result;
	}

	public boolean allow( User user ) {
		LocalDate today = LocalDate.now();

		if ( summaryUsageRepository.tryIncrement( user, today, dailyLimit ) > 0 ) {
			logger.debug( "User: {} has not exceeded daily limit, allowing summary creation", user.getId() );
			return true;
		}

		try {
			summaryUsageService.insertNewUsage( user );
			logger.debug( "Successfully inserted new usage record, allowing summary creation" );
			return true;
		}
		catch ( DataIntegrityViolationException e ) {
			boolean allowed = summaryUsageRepository.tryIncrement( user, today, dailyLimit ) > 0;
			logger.debug( "User: {} is {} to create a summary after retry", user.getId(), allowed ? "allowed" : "not allowed" );
			return allowed;
		}
	}

	public int remaining( User user ) {
		int remaining = summaryUsageRepository.findByUserAndUsageDate( user, LocalDate.now() )
				.map( u -> Math.max( 0, dailyLimit - u.getCount() ) )
				.orElse( dailyLimit );
		logger.debug( "User: {} has {} summaries remaining for today", user.getId(), remaining );
		return remaining;
	}
}
