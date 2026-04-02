package com.project.monitoring.rules;

import com.project.monitoring.entity.HealthMetric;
import com.project.monitoring.entity.MonitoringRule;
import com.project.monitoring.repository.MonitoringRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RuleEvaluationEngine {

    private final MonitoringRuleRepository monitoringRuleRepository;

    public List<MonitoringRule> evaluate(HealthMetric metric) {
        List<MonitoringRule> activeRules = monitoringRuleRepository.findByIsActiveTrue();
        List<MonitoringRule> violatedRules = new ArrayList<>();

        for (MonitoringRule rule : activeRules) {
            boolean violated = isRuleViolated(metric, rule);

            if (violated) {
                violatedRules.add(rule);
                log.warn("Rule violated: Rule ID={} Metric={} Threshold={} Service={}",
                        rule.getId(),
                        rule.getMetricName(),
                        rule.getThresholdValue(),
                        metric.getService().getName());
            }
        }

        return violatedRules;
    }

    private boolean isRuleViolated(HealthMetric metric, MonitoringRule rule) {
        return switch (rule.getMetricName()) {
            case CPU_USAGE -> compareNumeric(metric.getCpuUsage(), rule);
            case MEMORY_USAGE -> compareNumeric(metric.getMemoryUsage(), rule);
            case RESPONSE_TIME_MS -> compareNumeric(metric.getResponseTimeMs(), rule);
            case LATENCY_MS -> compareNumeric(metric.getLatencyMs(), rule);
            case PACKET_LOSS -> compareNumeric(metric.getPacketLoss(), rule);
            case AVAILABILITY_STATUS -> compareString(metric.getAvailabilityStatus().name(), rule);
        };
    }

    private boolean compareNumeric(Double actualValue, MonitoringRule rule) {
        double threshold = Double.parseDouble(rule.getThresholdValue());

        return switch (rule.getOperator()) {
            case GREATER_THAN -> actualValue > threshold;
            case LESS_THAN -> actualValue < threshold;
            case EQUALS -> actualValue.equals(threshold);
        };
    }

    private boolean compareString(String actualValue, MonitoringRule rule) {
        return switch (rule.getOperator()) {
            case EQUALS -> actualValue.equalsIgnoreCase(rule.getThresholdValue());
            default -> false;
        };
    }
}