package com.skilltrack.backend.service;

import com.skilltrack.backend.dto.LearningLogDto;
import com.skilltrack.backend.dto.SummaryDto;
import com.skilltrack.backend.entity.Summary;
import com.skilltrack.backend.entity.User;
import com.skilltrack.backend.repository.SummaryRepository;
import com.skilltrack.backend.repository.SummaryUsageRepository;
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
		return summaries.stream().map( SummaryDto::from ).toList();
	}

	public SummaryDto.PagedSummariesResponse getPagedSummariesResponse( String from, String to, int page, Integer size, User user ) {
		LocalDateTime dtFrom = from != null ? LocalDate.parse( from ).atStartOfDay() : null;
		LocalDateTime dtTo = to != null ? LocalDate.parse( to ).atTime( LocalTime.MAX ) : null;
		Pageable pageable;
		if ( size == null ) {
			pageable = Pageable.unpaged();
		}
		else {
			pageable = PageRequest.of( page, size, Sort.by( "createdAt" ).descending() );
		}
		Page<Summary> summaryPage = getSummaries( user, dtFrom, dtTo, pageable );
		List<SummaryDto> content = summaryPage.getContent().stream()
				.map( SummaryDto::from )
				.toList();

		return new SummaryDto.PagedSummariesResponse( content, summaryPage.getNumber(), summaryPage.getSize(), summaryPage.getTotalPages(), summaryPage.getTotalElements() );
	}

	public Page<Summary> getSummaries( User user, LocalDateTime from, LocalDateTime to, Pageable pageable ) {
		if ( from != null && to != null ) {
			return summaryRepository.findByUserAndCreatedAtBetween( user, from, to, pageable );
		}

		return summaryRepository.findByUser( user, pageable );
	}

	public SummaryDto summarizeWithLimitCheck( User user, List<LearningLogDto> logs ) {
		if ( !allow( user ) ) {
			throw new ResponseStatusException( HttpStatus.TOO_MANY_REQUESTS );
		}
		List<String> contents = LearningLogDto.toContentList( logs );
		String summaryText = summarize( user.getUsername(), contents );
		Summary saved = summaryRepository.save( new Summary( null, user, summaryText, LocalDateTime.now() ) );

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

		return chatModel.call( new Prompt( promptText ) ).getResult().getOutput().getText();
	}

	public boolean allow( User user ) {
		LocalDate today = LocalDate.now();
		if ( summaryUsageRepository.tryIncrement( user, today, dailyLimit ) > 0 ) return true;
		try {
			summaryUsageService.insertNewUsage( user );
			return true;
		}
		catch ( DataIntegrityViolationException e ) {
			return summaryUsageRepository.tryIncrement( user, today, dailyLimit ) > 0;
		}
	}

	public int remaining( User user ) {
		return summaryUsageRepository.findByUserAndUsageDate( user, LocalDate.now() )
				.map( u -> Math.max( 0, dailyLimit - u.getCount() ) )
				.orElse( dailyLimit );
	}
}
