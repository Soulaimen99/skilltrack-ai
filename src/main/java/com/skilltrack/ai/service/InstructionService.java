package com.skilltrack.ai.service;

import com.skilltrack.ai.dto.InstructionDto;
import com.skilltrack.ai.dto.LearningLogDto;
import com.skilltrack.ai.entity.Instruction;
import com.skilltrack.ai.entity.LearningGoal;
import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.repository.InstructionRepository;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstructionService {

	private final OpenAiChatModel chatModel;

	private final InstructionRepository instructionRepository;

	public InstructionService( OpenAiChatModel chatModel, InstructionRepository instructionRepository ) {
		this.chatModel = chatModel;
		this.instructionRepository = instructionRepository;
	}

	public List<InstructionDto> getAllInstructions( User user ) {
		List<Instruction> instructions = instructionRepository.findByUser( user );
		return instructions.stream().map( InstructionDto::from ).toList();
	}

	public InstructionDto generateInstruction( User user, LearningGoal goal, List<LearningLogDto> logs ) {
		String adviceText = generateAdvice( user.getUsername(), goal.getTitle(), LearningLogDto.toContentList( logs ) );
		Instruction instruction = instructionRepository.save( new Instruction( null, user, adviceText, null ) );
		return InstructionDto.from( instruction );
	}

	private String generateAdvice( String username, String goalTitle, List<String> reflections ) {
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

		return chatModel.call( new Prompt( promptText ) ).getResult().getOutput().getText();
	}
}
