package com.skilltrack.ai.service;

import com.skilltrack.ai.model.LearningLog;
import com.skilltrack.ai.model.User;
import com.skilltrack.ai.repository.LearningLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LearningLogService {

	private final LearningLogRepository learningLogRepository;

	public LearningLogService( LearningLogRepository learningLogRepository ) {
		this.learningLogRepository = learningLogRepository;
	}

	public List<LearningLog> getLogs( User user ) {
		return learningLogRepository.findByUser( user );
	}

	public LearningLog addLog( User user, LearningLog log ) {
		log.setUser( user );
		return learningLogRepository.save( log );
	}

	public void deleteLog( User user, Long id ) {
		Optional<LearningLog> log = learningLogRepository.findById( id );
		log.ifPresent( l -> {
			if ( l.getUser().getId().equals( user.getId() ) ) {
				learningLogRepository.deleteById( id );
			}
		} );
	}
}
