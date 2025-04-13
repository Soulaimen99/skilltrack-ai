package com.skilltrack.ai.service;

import com.skilltrack.ai.config.OpenAiProperties;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SummaryService {

	private final OpenAiService openAiService;
	private final OpenAiProperties openAiProperties;

	public SummaryService( OpenAiService openAiService, OpenAiProperties openAiProperties ) {
		this.openAiService = openAiService;
		this.openAiProperties = openAiProperties;
	}

	public String summarize( String username, List<String> logs ) {
		String reflections = String.join( "\n", logs );

		String userPrompt = String.format( """
				You are an intelligent learning assistant for a user named %s.
				Summarize the following personal learning reflections in a friendly and motivational tone.
				Reflections:
				%s
				""", username, reflections );

		List<ChatMessage> messages = new ArrayList<>();
		messages.add( new ChatMessage( "system", "You are a helpful assistant that summarizes learning progress." ) );
		messages.add( new ChatMessage( "user", userPrompt ) );

		ChatCompletionRequest request = ChatCompletionRequest.builder()
				.model( openAiProperties.getModel() )
				.messages( messages )
				.temperature( openAiProperties.getTemperature() )
				.maxTokens( openAiProperties.getMaxTokens() )
				.build();

		return openAiService.createChatCompletion( request )
				.getChoices()
				.getFirst()
				.getMessage()
				.getContent()
				.trim();
	}
}
