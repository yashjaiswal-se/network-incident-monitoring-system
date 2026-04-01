package com.project.monitoring.dto.metrics;

import com.project.monitoring.enums.AvailabilityStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateHealthMetricRequest {

    @NotNull
    private Long serviceId;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double cpuUsage;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double memoryUsage;

    @NotNull
    @DecimalMin("0.0")
    private Double responseTimeMs;

    @NotNull
    @DecimalMin("0.0")
    private Double latencyMs;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double packetLoss;

    @NotNull
    private AvailabilityStatus availabilityStatus;
}