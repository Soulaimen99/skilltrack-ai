package com.skilltrack.ai.service;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SummaryService {

	private static final String SUMMARY_PROMPT = """
			You are an intelligent learning assistant for a user named %s.
			Summarize the following personal learning reflections in a friendly and motivational tone.
			Include a short "Next Step" section with 1 action the user should take next.
			
			Reflections:
			%s
			""";

	private final OpenAiChatModel chatModel;

	@Autowired
	public SummaryService( OpenAiChatModel chatModel ) {
		this.chatModel = chatModel;
	}

	/**
	 * Summarizes the given learning logs for the specified user.
	 *
	 * @param username the username (for context/tracking)
	 * @param contents a list of non-blank log entries
	 * @return a concise, bullet‑point summary
	 */
	public String summarize( String username, List<String> contents ) {
		String logsBlock = contents.stream()
				.map( line -> "- " + line )
				.collect( Collectors.joining( "\n" ) );
		String promptText = String.format( SUMMARY_PROMPT, username, logsBlock );
		Prompt prompt = new Prompt( promptText );
		ChatResponse response = chatModel.call( prompt );

		return response.getResult().getOutput().getText();
	}
}
