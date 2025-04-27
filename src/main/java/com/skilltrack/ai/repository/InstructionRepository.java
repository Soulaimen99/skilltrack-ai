package com.skilltrack.ai.repository;

import com.skilltrack.ai.entity.Instruction;
import com.skilltrack.ai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InstructionRepository extends JpaRepository<Instruction, UUID> {

	List<Instruction> findByUser( User user );
}

