package com.skilltrack.ai.service;

import com.skilltrack.ai.model.LearningLog;
import com.skilltrack.ai.model.User;
import com.skilltrack.ai.repository.LearningLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LearningLogService {

	private final LearningLogRepository learningLogRepository;

	public LearningLogService( LearningLogRepository learningLogRepository ) {
		this.learningLogRepository = learningLogRepository;
	}

	public List<LearningLog> getLogs( User user, LocalDateTime dtFrom, LocalDateTime dtTo ) {
		List<LearningLog> learningLogs = learningLogRepository.findByUser( user );
		if ( dtFrom != null || dtTo != null ) {
			learningLogs = learningLogs.stream().filter( log ->
					( dtFrom == null || log.getDate().isAfter( dtFrom ) ) &&
							( dtTo == null || log.getDate().isBefore( dtTo ) )
			).toList();
		}
		return learningLogs;
	}

	public LearningLog addLog( User user, LearningLog log ) {
		log.setUser( user );
		return learningLogRepository.save( log );
	}

	public boolean deleteLog( User user, UUID id ) {
		return learningLogRepository.findById( id )
				.filter( log -> log.getUser().getId().equals( user.getId() ) )
				.map( log -> {
					learningLogRepository.delete( log );
					return true;
				} )
				.orElse( false );
	}

}
