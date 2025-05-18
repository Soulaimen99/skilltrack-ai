package com.skilltrack.backend.service;

import com.skilltrack.backend.dto.InstructionDto;
import com.skilltrack.backend.dto.LearningLogDto;
import com.skilltrack.backend.entity.Instruction;
import com.skilltrack.backend.entity.LearningGoal;
import com.skilltrack.backend.entity.User;
import com.skilltrack.backend.repository.InstructionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructionService {

	private static final Logger logger = LoggerFactory.getLogger( InstructionService.class );

	private final OpenAiChatModel chatModel;

	private final InstructionRepository instructionRepository;

	public List<InstructionDto> getAllInstructions( User user ) {
		logger.info( "Retrieving all instructions for user: {}", user.getId() );
		List<Instruction> instructions = instructionRepository.findByUser( user );
		logger.info( "Retrieved {} instructions for user: {}", instructions.size(), user.getId() );
		return instructions.stream().map( InstructionDto::from ).toList();
	}

	public InstructionDto generateInstruction( User user, LearningGoal goal, List<LearningLogDto> logs ) {
		logger.info( "Generating instruction for user: {} with goal: {}", user.getId(), goal.getId() );

		String adviceText = generateAdvice( user.getUsername(), goal.getTitle(), LearningLogDto.toContentList( logs ) );
		Instruction instruction = instructionRepository.save( new Instruction( null, user, goal, adviceText, null ) );

		logger.info( "Successfully generated and saved instruction with ID: {}", instruction.getId() );
		return InstructionDto.from( instruction );
	}

	private String generateAdvice( String username, String goalTitle, List<String> reflections ) {
		logger.debug( "Generating advice for user: {} with goal: {}", username, goalTitle );

		String reflectionsText = reflections.stream()
				.map( r -> "- " + r )
				.collect( Collectors.joining( "\n" ) );

		String promptText = String.format( """
				You are a learning coach for %s.
				The user is working on the goal: "%s".
				Based on this goal and the following reflections, suggest 2-3 next learning actions.
				Keep it actionable, motivating, and tailored to the goal.
				
				Reflections:
				%s
				""", username, goalTitle, reflectionsText );

		String result = chatModel.call( new Prompt( promptText ) ).getResult().getOutput().getText();
		logger.debug( "Successfully generated advice from AI model" );

		return result;
	}
}
