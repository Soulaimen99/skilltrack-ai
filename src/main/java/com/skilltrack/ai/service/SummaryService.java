package com.skilltrack.ai.service;

import com.skilltrack.ai.model.Summary;
import com.skilltrack.ai.model.User;
import com.skilltrack.ai.repository.SummaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SummaryService {

	private final SummaryRepository summaryRepository;

	public SummaryService( SummaryRepository summaryRepository ) {
		this.summaryRepository = summaryRepository;
	}

	public List<Summary> getSummaries( User user ) {
		return summaryRepository.findByUserOrderByCreatedAtDesc( user );
	}

	public void addSummary( User user, String content ) {
		Summary summary = new Summary();
		summary.setUser( user );
		summary.setContent( content );
		summaryRepository.save( summary );
	}

	public void deleteSummary( User user, Long id ) {
		Optional<Summary> summary = summaryRepository.findById( id );
		summary.ifPresent( s -> {
			if ( s.getUser().getId().equals( user.getId() ) ) {
				summaryRepository.deleteById( id );
			}
		} );
	}
}
