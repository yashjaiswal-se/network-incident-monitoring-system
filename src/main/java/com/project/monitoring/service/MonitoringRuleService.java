package com.project.monitoring.service;

import com.project.monitoring.dto.rule.CreateMonitoringRuleRequest;
import com.project.monitoring.dto.rule.MonitoringRuleResponse;
import com.project.monitoring.entity.MonitoringRule;
import com.project.monitoring.exception.ResourceNotFoundException;
import com.project.monitoring.repository.MonitoringRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitoringRuleService {

    private final MonitoringRuleRepository monitoringRuleRepository;

    public MonitoringRuleResponse createRule(CreateMonitoringRuleRequest request) {
        MonitoringRule rule = MonitoringRule.builder()
                .metricName(request.getMetricName())
                .operator(request.getOperator())
                .thresholdValue(request.getThresholdValue())
                .severity(request.getSeverity())
                .isActive(true)
                .build();

        MonitoringRule savedRule = monitoringRuleRepository.save(rule);
        return mapToResponse(savedRule);
    }

    public List<MonitoringRuleResponse> getAllRules() {
        return monitoringRuleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public MonitoringRuleResponse toggleRuleStatus(Long id) {
        MonitoringRule rule = monitoringRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found with id: " + id));

        rule.setIsActive(!rule.getIsActive());
        MonitoringRule updatedRule = monitoringRuleRepository.save(rule);

        return mapToResponse(updatedRule);
    }

    public List<MonitoringRule> getActiveRules() {
        return monitoringRuleRepository.findByIsActiveTrue();
    }

    private MonitoringRuleResponse mapToResponse(MonitoringRule rule) {
        return MonitoringRuleResponse.builder()
                .id(rule.getId())
                .metricName(rule.getMetricName())
                .operator(rule.getOperator())
                .thresholdValue(rule.getThresholdValue())
                .severity(rule.getSeverity())
                .isActive(rule.getIsActive())
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .build();
    }
}