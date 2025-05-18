package com.skilltrack.backend.service;

import com.skilltrack.backend.dto.LearningInsightsDto;
import com.skilltrack.backend.dto.LearningLogDto;
import com.skilltrack.backend.entity.LearningLog;
import com.skilltrack.backend.entity.User;
import com.skilltrack.backend.exception.ResourceNotFoundException;
import com.skilltrack.backend.repository.LearningLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningLogService {

	private static final Logger logger = LoggerFactory.getLogger( LearningLogService.class );

	private final LearningLogRepository learningLogRepository;

	public List<LearningLogDto> getAllLogs( User user ) {
		List<LearningLog> logs = learningLogRepository.findByUser( user );
		return logs.stream().map( LearningLogDto::from ).toList();
	}

	public LearningLogDto.PagedLogsResponse getPagedLogsResponse( String from, String to, int page, Integer size, UUID goalId, User user ) {
		LocalDateTime dtFrom = from != null ? LocalDate.parse( from ).atStartOfDay() : null;
		LocalDateTime dtTo = to != null ? LocalDate.parse( to ).atTime( LocalTime.MAX ) : null;
		Pageable pageable;
		if ( size == null ) {
			pageable = Pageable.unpaged();
		}
		else {
			pageable = PageRequest.of( page, size, Sort.by( "createdAt" ).descending() );
		}
		Page<LearningLog> logPage = getLogs( user, dtFrom, dtTo, goalId, pageable );
		List<LearningLogDto> content = logPage.getContent().stream()
				.map( LearningLogDto::from )
				.toList();

		return new LearningLogDto.PagedLogsResponse( content, logPage.getNumber(), logPage.getSize(), logPage.getTotalPages(), logPage.getTotalElements() );
	}

	public Page<LearningLog> getLogs( User user, LocalDateTime from, LocalDateTime to, UUID goalId, Pageable pageable ) {
		if ( goalId != null ) {
			if ( from != null && to != null ) {
				return learningLogRepository.findByUserAndGoalIdAndCreatedAtBetween( user, goalId, from, to, pageable );
			}
			else {
				return learningLogRepository.findByUserAndGoalId( user, goalId, pageable );
			}
		}
		else {
			if ( from != null && to != null ) {
				return learningLogRepository.findByUserAndCreatedAtBetween( user, from, to, pageable );
			}
			else {
				return learningLogRepository.findByUser( user, pageable );
			}
		}
	}

	public LearningLog addLog( LearningLog log ) {
		logger.info( "Adding new learning log for user: {}", log.getUser().getId() );
		LearningLog savedLog = learningLogRepository.save( log );
		logger.info( "Successfully added learning log with ID: {}", savedLog.getId() );
		return savedLog;
	}

	public LearningLog editLog( UUID id, LearningLog log ) {
		logger.info( "Editing learning log with ID: {}", id );
		LearningLog existingLog = learningLogRepository.findById( id )
				.orElseThrow( () -> new ResourceNotFoundException( "Log", id ) );
		existingLog.setContent( log.getContent() );
		existingLog.setTags( log.getTags() );

		LearningLog updatedLog = learningLogRepository.save( existingLog );
		logger.info( "Successfully updated learning log with ID: {}", id );
		return updatedLog;
	}

	public boolean deleteLog( User user, UUID id ) {
		logger.info( "Attempting to delete learning log with ID: {} for user: {}", id, user.getId() );
		boolean result = learningLogRepository.findById( id )
				.filter( log -> log.getUser().getId().equals( user.getId() ) )
				.map( l -> {
					learningLogRepository.delete( l );
					logger.info( "Successfully deleted learning log with ID: {}", id );
					return true;
				} )
				.orElse( false );

		if ( !result ) {
			logger.warn( "Failed to delete learning log with ID: {} - log not found or user not authorized", id );
		}
		return result;
	}

	public LocalDate getLastLogDate( User user ) {
		LocalDateTime last = learningLogRepository.findLastLogDateByUser( user );
		return last == null ? null : last.toLocalDate();
	}

	public LearningInsightsDto getInsights( User user ) {
		logger.info( "Generating learning insights for user: {}", user.getId() );
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime sevenDaysAgo = now.minusDays( 7 );
		LocalDateTime thirtyDaysAgo = now.minusDays( 30 );

		List<LearningLog> recentLogs = learningLogRepository.findByUserAndCreatedAtAfter( user, thirtyDaysAgo );
		logger.debug( "Retrieved {} logs from the last 30 days for user: {}", recentLogs.size(), user.getId() );

		int logsLast7 = ( int ) recentLogs.stream()
				.filter( log -> log.getCreatedAt().isAfter( sevenDaysAgo ) )
				.count();

		int logsLast30 = recentLogs.size();

		String mostUsedTag = recentLogs.stream()
				.flatMap( log -> Arrays.stream( ( log.getTags() + "," ).split( "," ) ) )
				.map( String::trim )
				.filter( s -> !s.isBlank() )
				.collect( Collectors.groupingBy( tag -> tag, Collectors.counting() ) )
				.entrySet().stream()
				.max( Map.Entry.comparingByValue() )
				.map( Map.Entry::getKey )
				.orElse( null );

		LocalDate lastLogDate = getLastLogDate( user );
		int daysSinceLast = lastLogDate != null ? Period.between( lastLogDate, LocalDate.now() ).getDays() : -1;

		LearningInsightsDto insights = new LearningInsightsDto( logsLast7, logsLast30, mostUsedTag, daysSinceLast );
		logger.info( "Successfully generated learning insights for user: {}", user.getId() );
		return insights;
	}
}
