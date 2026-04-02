package com.project.monitoring.dto.rule;

import com.project.monitoring.enums.MetricName;
import com.project.monitoring.enums.RuleOperator;
import com.project.monitoring.enums.SeverityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMonitoringRuleRequest {

    @NotNull(message = "Metric name is required")
    private MetricName metricName;

    @NotNull(message = "Operator is required")
    private RuleOperator operator;

    @NotBlank(message = "Threshold value is required")
    private String thresholdValue;

    @NotNull(message = "Severity is required")
    private SeverityLevel severity;
}