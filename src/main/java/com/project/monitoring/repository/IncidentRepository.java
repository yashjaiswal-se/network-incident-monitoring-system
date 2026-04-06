package com.project.monitoring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.monitoring.entity.Incident;
import com.project.monitoring.enums.IncidentStatus;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findAllByOrderByCreatedAtDesc();

    List<Incident> findByStatusOrderByCreatedAtDesc(IncidentStatus status);

    boolean existsByServiceIdAndStatus(Long serviceId, IncidentStatus status);
}
