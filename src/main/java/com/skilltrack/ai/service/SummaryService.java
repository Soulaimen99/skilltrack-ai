package com.skilltrack.ai.service;

import com.skilltrack.ai.entity.User;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SummaryService {

	private final OpenAiChatModel chatModel;

	private final SummaryRateLimiter rateLimiter;

	public SummaryService( OpenAiChatModel chatModel, SummaryRateLimiter rateLimiter ) {
		this.chatModel = chatModel;
		this.rateLimiter = rateLimiter;
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
