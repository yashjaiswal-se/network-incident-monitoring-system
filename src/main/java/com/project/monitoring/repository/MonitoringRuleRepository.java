package com.project.monitoring.repository;

import com.project.monitoring.entity.MonitoringRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonitoringRuleRepository extends JpaRepository<MonitoringRule, Long> {
    List<MonitoringRule> findByIsActiveTrue();
}