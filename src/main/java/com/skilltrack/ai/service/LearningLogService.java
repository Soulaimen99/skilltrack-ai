package com.skilltrack.ai.service;

import com.skilltrack.ai.entity.LearningLog;
import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.repository.LearningLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LearningLogService {

	private final LearningLogRepository repo;

	public LearningLogService( LearningLogRepository repo ) {
		this.repo = repo;
	}

	public List<LearningLog> getLogs( User user, LocalDateTime from, LocalDateTime to ) {
		return repo.findByUser( user ).stream()
				.filter( log -> ( from == null || log.getCreatedAt().isAfter( from ) ) &&
						( to == null || log.getCreatedAt().isBefore( to ) ) )
				.toList();
	}

	public LearningLog addLog( LearningLog log ) {
		return repo.save( log );
	}

	public boolean deleteLog( User user, UUID id ) {
		return repo.findById( id ).filter( log -> log.getUser().getId().equals( user.getId() ) ).map( l -> {
			repo.delete( l );
			return true;
		} ).orElse( false );
	}
}