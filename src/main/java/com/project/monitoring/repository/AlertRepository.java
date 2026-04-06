package com.project.monitoring.repository;

import com.project.monitoring.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByIncidentIdOrderBySentAtDesc(Long incidentId);

}