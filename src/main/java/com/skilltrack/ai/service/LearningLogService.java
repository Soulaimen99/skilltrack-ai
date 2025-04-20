package com.skilltrack.ai.service;

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
import java.util.List;
import java.util.UUID;

@Service
public class LearningLogService {

	private final LearningLogRepository learningLogRepository;

	public LearningLogService( LearningLogRepository learningLogRepository ) {
		this.learningLogRepository = learningLogRepository;
	}

	public LearningLogDto.PagedLogsResponse getPagedLogsResponse( String from, String to, int page, int size, User user ) {
		LocalDateTime dtFrom = from != null ? LocalDate.parse( from ).atStartOfDay() : null;
		LocalDateTime dtTo = to != null ? LocalDate.parse( to ).atTime( LocalTime.MAX ) : null;
		Pageable pageable = PageRequest.of( page, size, Sort.by( "createdAt" ).descending() );
		Page<LearningLog> logPage = getLogs( user, dtFrom, dtTo, pageable );

		List<LearningLogDto> content = logPage.getContent().stream()
				.map( LearningLogDto::from )
				.toList();

		return new LearningLogDto.PagedLogsResponse( content, logPage.getNumber(), logPage.getSize(), logPage.getTotalPages(), logPage.getTotalElements() );
	}

	public Page<LearningLog> getLogs( User user, LocalDateTime from, LocalDateTime to, Pageable pageable ) {
		if ( from != null && to != null ) {
			return learningLogRepository.findByUserAndCreatedAtBetween( user, from, to, pageable );
		}
		return learningLogRepository.findByUser( user, pageable );
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
}