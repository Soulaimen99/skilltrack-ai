package com.skilltrack.ai.service;

import com.skilltrack.ai.entity.LearningLog;
import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.repository.LearningLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LearningLogService {

	private final LearningLogRepository learningLogRepository;

	public LearningLogService( LearningLogRepository learningLogRepository ) {
		this.learningLogRepository = learningLogRepository;
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

	public boolean deleteLog( User user, UUID id ) {
		return learningLogRepository.findById( id ).filter( log -> log.getUser().getId().equals( user.getId() ) ).map( l -> {
			learningLogRepository.delete( l );
			return true;
		} ).orElse( false );
	}
}