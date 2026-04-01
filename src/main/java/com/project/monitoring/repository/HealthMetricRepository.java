package com.project.monitoring.repository;

import com.project.monitoring.entity.HealthMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HealthMetricRepository extends JpaRepository<HealthMetric, Long> {

    List<HealthMetric> findByServiceIdOrderByRecordedAtDesc(Long serviceId);

    Optional<HealthMetric> findTopByServiceIdOrderByRecordedAtDesc(Long serviceId);
}