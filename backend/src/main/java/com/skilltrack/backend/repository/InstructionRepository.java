package com.skilltrack.backend.repository;

import com.skilltrack.backend.entity.Instruction;
import com.skilltrack.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InstructionRepository extends JpaRepository<Instruction, UUID> {

	List<Instruction> findByUser( User user );
}

