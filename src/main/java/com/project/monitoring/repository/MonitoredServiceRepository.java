package com.project.monitoring.repository;

import com.project.monitoring.entity.MonitoredService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoredServiceRepository extends JpaRepository<MonitoredService, Long> {
}