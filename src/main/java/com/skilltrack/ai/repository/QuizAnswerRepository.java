package com.skilltrack.ai.repository;

import com.skilltrack.ai.entity.QuizAnswer;
import com.skilltrack.ai.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, UUID> {

	Optional<QuizAnswer> findByQuizQuestion( QuizQuestion quizQuestion );

	long countByCorrect( boolean correct );

	long countByQuizQuestionIn( Iterable<QuizQuestion> quizQuestions );

	long countByQuizQuestionInAndCorrect( Iterable<QuizQuestion> quizQuestions, boolean correct );
}