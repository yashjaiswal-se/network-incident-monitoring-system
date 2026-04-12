package com.project.monitoring.repository;

import com.project.monitoring.entity.MonitoredService;
import com.project.monitoring.enums.ServiceStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoredServiceRepository extends JpaRepository<MonitoredService, Long> {
    long countByStatus(ServiceStatus status);
}