package com.project.monitoring.service;

import com.project.monitoring.dto.metrics.CreateHealthMetricRequest;
import com.project.monitoring.dto.metrics.HealthMetricResponse;
import com.project.monitoring.entity.HealthMetric;
import com.project.monitoring.entity.MonitoredService;
import com.project.monitoring.entity.MonitoringRule;
import com.project.monitoring.enums.AvailabilityStatus;
import com.project.monitoring.exception.ResourceNotFoundException;
import com.project.monitoring.repository.HealthMetricRepository;
import com.project.monitoring.repository.MonitoredServiceRepository;
import com.project.monitoring.rules.RuleEvaluationEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthMetricsService {

    private final HealthMetricRepository healthMetricRepository;
    private final MonitoredServiceRepository monitoredServiceRepository;
    private final RuleEvaluationEngine ruleEvaluationEngine;

    public HealthMetricResponse submitMetric(CreateHealthMetricRequest request) {
        log.info("Submitting health metric for serviceId={}", request.getServiceId());

        MonitoredService service = getServiceOrThrow(request.getServiceId());
        return saveMetric(service, request);
    }

    @Transactional(readOnly = true)
    public List<HealthMetricResponse> getMetricsByService(Long serviceId) {
        log.info("Fetching health metrics for serviceId={}", serviceId);

        getServiceOrThrow(serviceId);

        return healthMetricRepository.findByServiceIdOrderByRecordedAtDesc(serviceId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public HealthMetricResponse simulateMetric(Long serviceId) {
        log.info("Simulating health metric for serviceId={}", serviceId);

        MonitoredService service = getServiceOrThrow(serviceId);
        CreateHealthMetricRequest simulatedRequest = generateSimulatedMetricRequest(serviceId);

        return saveMetric(service, simulatedRequest);
    }

    private HealthMetricResponse saveMetric(MonitoredService service, CreateHealthMetricRequest request) {
        HealthMetric metric = HealthMetric.builder()
                .service(service)
                .cpuUsage(request.getCpuUsage())
                .memoryUsage(request.getMemoryUsage())
                .responseTimeMs(request.getResponseTimeMs())
                .latencyMs(request.getLatencyMs())
                .packetLoss(request.getPacketLoss())
                .availabilityStatus(request.getAvailabilityStatus())
                .recordedAt(LocalDateTime.now())
                .build();

        HealthMetric savedMetric = healthMetricRepository.save(metric);

        log.info("Health metric saved successfully for service={} metricId={}",
                service.getName(), savedMetric.getId());

        List<MonitoringRule> violatedRules = ruleEvaluationEngine.evaluate(savedMetric);

        if (!violatedRules.isEmpty()) {
            log.warn("Metric submission violated {} rule(s) for service={}",
                    violatedRules.size(),
                    service.getName());

            violatedRules.forEach(rule ->
                    log.warn("Violated Rule -> id={}, metric={}, operator={}, threshold={}, severity={}",
                            rule.getId(),
                            rule.getMetricName(),
                            rule.getOperator(),
                            rule.getThresholdValue(),
                            rule.getSeverity())
            );
        } else {
            log.info("No rule violations detected for service={}", service.getName());
        }

        return mapToResponse(savedMetric);
    }

    private CreateHealthMetricRequest generateSimulatedMetricRequest(Long serviceId) {
        Random random = new Random();

        CreateHealthMetricRequest request = new CreateHealthMetricRequest();
        request.setServiceId(serviceId);
        request.setCpuUsage(20.0 + (75.0 * random.nextDouble()));
        request.setMemoryUsage(30.0 + (60.0 * random.nextDouble()));
        request.setResponseTimeMs(100.0 + (2900.0 * random.nextDouble()));
        request.setLatencyMs(10.0 + (490.0 * random.nextDouble()));
        request.setPacketLoss(0.0 + (20.0 * random.nextDouble()));
        request.setAvailabilityStatus(random.nextInt(10) < 8
                ? AvailabilityStatus.UP
                : AvailabilityStatus.DOWN);

        return request;
    }

    private MonitoredService getServiceOrThrow(Long serviceId) {
        return monitoredServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));
    }

    private HealthMetricResponse mapToResponse(HealthMetric metric) {
        return HealthMetricResponse.builder()
                .id(metric.getId())
                .serviceId(metric.getService().getId())
                .serviceName(metric.getService().getName())
                .cpuUsage(metric.getCpuUsage())
                .memoryUsage(metric.getMemoryUsage())
                .responseTimeMs(metric.getResponseTimeMs())
                .latencyMs(metric.getLatencyMs())
                .packetLoss(metric.getPacketLoss())
                .availabilityStatus(metric.getAvailabilityStatus())
                .recordedAt(metric.getRecordedAt())
                .build();
    }
}