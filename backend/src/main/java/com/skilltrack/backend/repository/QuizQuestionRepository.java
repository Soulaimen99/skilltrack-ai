package com.skilltrack.backend.repository;

import com.skilltrack.backend.entity.Quiz;
import com.skilltrack.backend.entity.QuizQuestion;
import com.skilltrack.backend.entity.QuizType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, UUID> {

	List<QuizQuestion> findByQuiz( Quiz quiz );

	List<QuizQuestion> findByQuizAndType( Quiz quiz, QuizType type );

	long countByQuiz( Quiz quiz );

	long countByQuizAndType( Quiz quiz, QuizType type );
}