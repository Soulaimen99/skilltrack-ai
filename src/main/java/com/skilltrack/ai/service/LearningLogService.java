package com.skilltrack.ai.service;

import com.skilltrack.ai.dto.LearningInsightsDto;
import com.skilltrack.ai.dto.LearningLogDto;
import com.skilltrack.ai.entity.LearningLog;
import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.repository.LearningLogRepository;
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
public class LearningLogService {

	private final LearningLogRepository learningLogRepository;

	public LearningLogService( LearningLogRepository learningLogRepository ) {
		this.learningLogRepository = learningLogRepository;
	}

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
		return learningLogRepository.save( log );
	}

	public LearningLog editLog( UUID id, LearningLog log ) {
		LearningLog existingLog = learningLogRepository.findById( id ).orElseThrow(
				() -> new IllegalArgumentException( "Log with id " + id + " does not exist" )
		);
		existingLog.setContent( log.getContent() );
		existingLog.setTags( log.getTags() );

		return learningLogRepository.save( existingLog );
	}

	public boolean deleteLog( User user, UUID id ) {
		return learningLogRepository.findById( id ).filter( log -> log.getUser().getId().equals( user.getId() ) ).map( l -> {
			learningLogRepository.delete( l );
			return true;
		} ).orElse( false );
	}

	public LocalDate getLastLogDate( User user ) {
		LocalDateTime last = learningLogRepository.findLastLogDateByUser( user );
		return last == null ? null : last.toLocalDate();
	}

	public LearningInsightsDto getInsights( User user ) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime sevenDaysAgo = now.minusDays( 7 );
		LocalDateTime thirtyDaysAgo = now.minusDays( 30 );

		List<LearningLog> recentLogs = learningLogRepository.findByUserAndCreatedAtAfter( user, thirtyDaysAgo );

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

		return new LearningInsightsDto( logsLast7, logsLast30, mostUsedTag, daysSinceLast );
	}
}
