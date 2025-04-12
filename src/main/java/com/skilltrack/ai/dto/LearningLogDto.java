package com.skilltrack.ai.dto;

import java.time.LocalDate;

public record LearningLogDto( Long id, String username, String content, String tags, LocalDate date ) {
}
