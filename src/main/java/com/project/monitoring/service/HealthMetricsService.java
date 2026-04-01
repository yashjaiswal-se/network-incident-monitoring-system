package com.project.monitoring.service;

import com.project.monitoring.dto.metrics.CreateHealthMetricRequest;
import com.project.monitoring.dto.metrics.HealthMetricResponse;
import com.project.monitoring.entity.HealthMetric;
import com.project.monitoring.entity.MonitoredService;
import com.project.monitoring.exception.ResourceNotFoundException;
import com.project.monitoring.repository.HealthMetricRepository;
import com.project.monitoring.repository.MonitoredServiceRepository;
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

    public HealthMetricResponse submitMetric(CreateHealthMetricRequest request) {
        log.info("Submitting health metric for serviceId={}", request.getServiceId());
        return saveMetric(request);
    }

    @Transactional(readOnly = true)
    public List<HealthMetricResponse> getMetricsByService(Long serviceId) {
        monitoredServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));

        return healthMetricRepository.findByServiceIdOrderByRecordedAtDesc(serviceId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public HealthMetricResponse simulateMetric(Long serviceId) {
        log.info("Simulating health metric for serviceId={}", serviceId);

        monitoredServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));

        Random random = new Random();

        CreateHealthMetricRequest request = new CreateHealthMetricRequest();
        request.setServiceId(serviceId);
        request.setCpuUsage(20.0 + (75.0 * random.nextDouble()));
        request.setMemoryUsage(30.0 + (60.0 * random.nextDouble()));
        request.setResponseTimeMs(100.0 + (2900.0 * random.nextDouble()));
        request.setLatencyMs(10.0 + (490.0 * random.nextDouble()));
        request.setPacketLoss(0.0 + (20.0 * random.nextDouble()));
        request.setAvailabilityStatus(random.nextInt(10) < 8
                ? com.project.monitoring.enums.AvailabilityStatus.UP
                : com.project.monitoring.enums.AvailabilityStatus.DOWN);

        return saveMetric(request);
    }

    private HealthMetricResponse saveMetric(CreateHealthMetricRequest request) {
        MonitoredService service = monitoredServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + request.getServiceId()));

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
        return mapToResponse(savedMetric);
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