package com.skilltrack.ai.service;

import com.skilltrack.ai.dto.SummaryDto;
import com.skilltrack.ai.entity.Summary;
import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.repository.SummaryRepository;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
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

	private final SummaryRateLimiter rateLimiter;

	private final SummaryRepository summaryRepository;

	public SummaryService( OpenAiChatModel chatModel, SummaryRateLimiter rateLimiter, SummaryRepository summaryRepository ) {
		this.chatModel = chatModel;
		this.rateLimiter = rateLimiter;
		this.summaryRepository = summaryRepository;
	}

	public SummaryDto.PagedSummariesResponse getPagedSummariesResponse( String from, String to, int page, int size, User user ) {
		LocalDateTime dtFrom = from != null ? LocalDate.parse( from ).atStartOfDay() : null;
		LocalDateTime dtTo = to != null ? LocalDate.parse( to ).atTime( LocalTime.MAX ) : null;
		Pageable pageable = PageRequest.of( page, size, Sort.by( "createdAt" ).descending() );
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

	public SummaryResult summarizeWithLimitCheck( User user, List<String> contents ) {
		if ( !rateLimiter.allow( user ) ) throw new ResponseStatusException( HttpStatus.TOO_MANY_REQUESTS );
		String summary = summarize( user.getUsername(), contents );
		return new SummaryResult( summary, rateLimiter.remaining( user ) );
	}

	public String summarize( String username, List<String> contents ) {
		String promptText = String.format( """
				You are an intelligent learning assistant for a user named %s.
				Summarize the following personal learning reflections in a friendly and motivational tone.
				Include a short "Next Step" section with 1 action the user should take next.
				
				Reflections:
				%s
				""", username, contents.stream().map( s -> "- " + s ).collect( Collectors.joining( "\n" ) ) );

		return chatModel.call( new Prompt( promptText ) ).getResult().getOutput().getText();
	}

	public record SummaryResult( String summary, int remaining ) {

	}
}
