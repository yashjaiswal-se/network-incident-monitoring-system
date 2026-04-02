package com.project.monitoring.dto.rule;

import com.project.monitoring.enums.MetricName;
import com.project.monitoring.enums.RuleOperator;
import com.project.monitoring.enums.SeverityLevel;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MonitoringRuleResponse {
    private Long id;
    private MetricName metricName;
    private RuleOperator operator;
    private String thresholdValue;
    private SeverityLevel severity;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}