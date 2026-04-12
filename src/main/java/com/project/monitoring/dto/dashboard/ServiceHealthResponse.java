package com.project.monitoring.dto.dashboard;

import com.project.monitoring.enums.AvailabilityStatus;

public class ServiceHealthResponse {

    private final Long serviceId;
    private final String serviceName;
    private final AvailabilityStatus latestAvailabilityStatus;
    private final Double latestCpuUsage;
    private final Double latestResponseTimeMs;
    private final String healthState;

    public ServiceHealthResponse(Long serviceId,
                                 String serviceName,
                                 AvailabilityStatus latestAvailabilityStatus,
                                 Double latestCpuUsage,
                                 Double latestResponseTimeMs,
                                 String healthState) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.latestAvailabilityStatus = latestAvailabilityStatus;
        this.latestCpuUsage = latestCpuUsage;
        this.latestResponseTimeMs = latestResponseTimeMs;
        this.healthState = healthState;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public AvailabilityStatus getLatestAvailabilityStatus() {
        return latestAvailabilityStatus;
    }

    public Double getLatestCpuUsage() {
        return latestCpuUsage;
    }

    public Double getLatestResponseTimeMs() {
        return latestResponseTimeMs;
    }

    public String getHealthState() {
        return healthState;
    }
}