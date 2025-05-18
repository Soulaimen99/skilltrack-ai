package com.skilltrack.backend.service;

import com.skilltrack.backend.dto.LearningGoalDto;
import com.skilltrack.backend.entity.LearningGoal;
import com.skilltrack.backend.entity.User;
import com.skilltrack.backend.exception.ResourceNotFoundException;
import com.skilltrack.backend.repository.LearningGoalRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LearningGoalService {

	private static final Logger logger = LoggerFactory.getLogger( LearningGoalService.class );

	private final LearningGoalRepository learningGoalRepository;

	public LearningGoal getByIdForUser( User user, UUID goalId ) {
		logger.info( "Retrieving goal with ID: {} for user: {}", goalId, user.getId() );

		LearningGoal goal = learningGoalRepository.findById( goalId )
				.orElseThrow( () -> new ResourceNotFoundException( "Goal", goalId ) );

		if ( !goal.getUser().getId().equals( user.getId() ) ) {
			throw new AccessDeniedException( "You cannot access this goal" );
		}

		logger.debug( "Successfully retrieved goal with ID: {} for user: {}", goalId, user.getId() );
		return goal;
	}


	public LearningGoalDto.PagedGoalsResponse getPagedGoalsResponse( String from, String to, int page, Integer size, User user ) {
		logger.info( "Retrieving paged goals with parameters: from={}, to={}, page={}, size={} for user: {}", from, to, page, size, user.getId() );

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

		Page<LearningGoal> goalPage = getGoals( user, dtFrom, dtTo, pageable );
		List<LearningGoalDto> content = goalPage.getContent().stream()
				.map( LearningGoalDto::from )
				.toList();

		logger.info( "Retrieved {} goals (page {} of {}) for user: {}",
				goalPage.getNumberOfElements(), goalPage.getNumber() + 1, goalPage.getTotalPages(), user.getId() );

		return new LearningGoalDto.PagedGoalsResponse( content, goalPage.getNumber(), goalPage.getSize(), goalPage.getTotalPages(), goalPage.getTotalElements() );
	}

	public Page<LearningGoal> getGoals( User user, LocalDateTime from, LocalDateTime to, Pageable pageable ) {
		logger.debug( "Fetching goals for user: {} with date range: {} to {}", user.getId(), from, to );

		if ( from != null && to != null ) {
			logger.debug( "Using date range filter for goals" );
			return learningGoalRepository.findByUserAndCreatedAtBetween( user, from, to, pageable );
		}

		logger.debug( "Fetching all goals without date range filter" );
		return learningGoalRepository.findByUser( user, pageable );
	}

	public LearningGoal addGoal( LearningGoal goal ) {
		logger.info( "Adding new learning goal for user: {}", goal.getUser().getId() );
		LearningGoal savedGoal = learningGoalRepository.save( goal );
		logger.info( "Successfully added learning goal with ID: {}", savedGoal.getId() );
		return savedGoal;
	}

	public LearningGoal editGoal( UUID id, LearningGoal goal ) {
		logger.info( "Editing learning goal with ID: {}", id );

		LearningGoal existingGoal = learningGoalRepository.findById( id )
				.orElseThrow( () -> new ResourceNotFoundException( "Goal", id ) );

		existingGoal.setTitle( goal.getTitle() );
		existingGoal.setDescription( goal.getDescription() );

		LearningGoal updatedGoal = learningGoalRepository.save( existingGoal );
		logger.info( "Successfully updated learning goal with ID: {}", id );

		return updatedGoal;
	}

	public boolean deleteGoal( User user, UUID id ) {
		logger.info( "Attempting to delete learning goal with ID: {} for user: {}", id, user.getId() );

		boolean result = learningGoalRepository.findById( id )
				.filter( goal -> goal.getUser().getId().equals( user.getId() ) )
				.map( l -> {
							learningGoalRepository.delete( l );
							logger.info( "Successfully deleted learning goal with ID: {}", id );
							return true;
						}
				).orElse( false );

		if ( !result ) {
			logger.warn( "Failed to delete learning goal with ID: {} - goal not found or user not authorized", id );
		}

		return result;
	}
}
