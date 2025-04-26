package com.skilltrack.ai.service;

import com.skilltrack.ai.dto.LearningGoalDto;
import com.skilltrack.ai.entity.LearningGoal;
import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.repository.LearningGoalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class LearningGoalService {

	private final LearningGoalRepository learningGoalRepository;

	public LearningGoalService( LearningGoalRepository learningGoalRepository ) {
		this.learningGoalRepository = learningGoalRepository;
	}

	public LearningGoal getByIdForUser( User user, UUID goalId ) {
		LearningGoal goal = learningGoalRepository.findById( goalId )
				.orElseThrow( () -> new ResponseStatusException( HttpStatus.NOT_FOUND, "Goal not found" ) );

		if ( !goal.getUser().getId().equals( user.getId() ) ) {
			throw new AccessDeniedException( "You cannot access this goal" );
		}

		return goal;
	}


	public List<LearningGoalDto> getAllGoals( User user ) {
		List<LearningGoal> logs = learningGoalRepository.findByUser( user );
		return logs.stream().map( LearningGoalDto::from ).toList();
	}

	public LearningGoalDto.PagedGoalsResponse getPagedGoalsResponse( String from, String to, int page, Integer size, User user ) {
		LocalDateTime dtFrom = from != null ? LocalDate.parse( from ).atStartOfDay() : null;
		LocalDateTime dtTo = to != null ? LocalDate.parse( to ).atTime( LocalTime.MAX ) : null;
		Pageable pageable;
		if ( size == null ) {
			pageable = Pageable.unpaged();
		}
		else {
			pageable = PageRequest.of( page, size, Sort.by( "createdAt" ).descending() );
		}
		Page<LearningGoal> goalPage = getGoals( user, dtFrom, dtTo, pageable );
		List<LearningGoalDto> content = goalPage.getContent().stream()
				.map( LearningGoalDto::from )
				.toList();

		return new LearningGoalDto.PagedGoalsResponse( content, goalPage.getNumber(), goalPage.getSize(), goalPage.getTotalPages(), goalPage.getTotalElements() );
	}

	public Page<LearningGoal> getGoals( User user, LocalDateTime from, LocalDateTime to, Pageable pageable ) {
		if ( from != null && to != null ) {
			return learningGoalRepository.findByUserAndCreatedAtBetween( user, from, to, pageable );
		}

		return learningGoalRepository.findByUser( user, pageable );
	}

	public LearningGoal addGoal( LearningGoal goal ) {
		return learningGoalRepository.save( goal );
	}

	public LearningGoal editGoal( UUID id, LearningGoal goal ) {
		LearningGoal existingGoal = learningGoalRepository.findById( id ).orElseThrow(
				() -> new IllegalArgumentException( "Goal with id " + id + " does not exist" )
		);
		existingGoal.setTitle( goal.getTitle() );
		existingGoal.setDescription( goal.getDescription() );

		return learningGoalRepository.save( existingGoal );
	}

	public boolean deleteGoal( User user, UUID id ) {
		return learningGoalRepository.findById( id )
				.filter( goal -> goal.getUser().getId().equals( user.getId() ) )
				.map( l -> {
							learningGoalRepository.delete( l );
							return true;
						}
				).orElse( false );
	}
}
