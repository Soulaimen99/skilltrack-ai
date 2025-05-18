package com.skilltrack.ai.repository;

import com.skilltrack.ai.entity.Quiz;
import com.skilltrack.ai.entity.QuizQuestion;
import com.skilltrack.ai.entity.QuizType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, UUID> {

	List<QuizQuestion> findByQuiz( Quiz quiz );

	List<QuizQuestion> findByQuizAndType( Quiz quiz, QuizType type );

	long countByQuiz( Quiz quiz );

	long countByQuizAndType( Quiz quiz, QuizType type );
}