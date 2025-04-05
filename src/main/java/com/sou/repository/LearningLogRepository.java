package com.sou.repository;

import jakarta.enterprise.context.ApplicationScoped;

import com.sou.model.LearningLog;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class LearningLogRepository implements PanacheRepository<LearningLog> {
}
