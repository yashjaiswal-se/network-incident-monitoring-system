package com.project.monitoring.dto.metrics;

import com.project.monitoring.enums.AvailabilityStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HealthMetricResponse {

    private Long id;
    private Long serviceId;
    private String serviceName;

    private Double cpuUsage;
    private Double memoryUsage;
    private Double responseTimeMs;
    private Double latencyMs;
    private Double packetLoss;

    private AvailabilityStatus availabilityStatus;
    private LocalDateTime recordedAt;
}