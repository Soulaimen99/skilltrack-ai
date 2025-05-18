package com.skilltrack.backend.repository;

import com.skilltrack.backend.entity.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, UUID> {

}