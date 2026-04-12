package com.project.monitoring.service;

import com.project.monitoring.dto.dashboard.DashboardSummaryResponse;
import com.project.monitoring.dto.dashboard.ServiceHealthResponse;
import com.project.monitoring.dto.incident.IncidentResponse;
import com.project.monitoring.entity.HealthMetric;
import com.project.monitoring.entity.Incident;
import com.project.monitoring.entity.MonitoredService;
import com.project.monitoring.enums.IncidentStatus;
import com.project.monitoring.enums.ServiceStatus;
import com.project.monitoring.repository.AlertRepository;
import com.project.monitoring.repository.HealthMetricRepository;
import com.project.monitoring.repository.IncidentRepository;
import com.project.monitoring.repository.MonitoredServiceRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DashboardService {

    private final MonitoredServiceRepository serviceRepository;
    private final IncidentRepository incidentRepository;
    private final AlertRepository alertRepository;
    private final HealthMetricRepository healthMetricRepository;

    public DashboardService(MonitoredServiceRepository serviceRepository,
                            IncidentRepository incidentRepository,
                            AlertRepository alertRepository,
                            HealthMetricRepository healthMetricRepository) {
        this.serviceRepository = serviceRepository;
        this.incidentRepository = incidentRepository;
        this.alertRepository = alertRepository;
        this.healthMetricRepository = healthMetricRepository;
    }

    public DashboardSummaryResponse getSummary() {

        log.info("DASHBOARD | Fetching summary");

        long totalServices = serviceRepository.count();
        long activeServices = serviceRepository.countByStatus(ServiceStatus.ACTIVE);

        long totalIncidents = incidentRepository.count();
        long activeIncidents = incidentRepository.countByStatus(IncidentStatus.OPEN);

        long totalAlerts = alertRepository.count();

        log.info("DASHBOARD | Summary computed successfully");

        return new DashboardSummaryResponse(
                totalServices,
                activeServices,
                totalIncidents,
                activeIncidents,
                totalAlerts
        );
    }

    @Transactional(readOnly = true)
    public List<IncidentResponse> getActiveIncidents() {

        log.info("DASHBOARD | Fetching active incidents");

        List<Incident> incidents =
                incidentRepository.findByStatusOrderByCreatedAtDesc(IncidentStatus.OPEN);

        List<IncidentResponse> response = incidents.stream()
                .map(incident -> IncidentResponse.builder()
                        .id(incident.getId())
                        .serviceId(incident.getService().getId())
                        .serviceName(incident.getService().getName())
                        .title(incident.getTitle())
                        .description(incident.getDescription())
                        .severity(incident.getSeverity())
                        .status(incident.getStatus())
                        .createdAt(incident.getCreatedAt())
                        .acknowledgedAt(incident.getAcknowledgedAt())
                        .resolvedAt(incident.getResolvedAt())
                        .build()
                )
                .toList();

        log.info("DASHBOARD | Active incidents fetched: {}", response.size());

        return response;
    }

    @Transactional(readOnly = true)
    public List<ServiceHealthResponse> getServicesHealth() {

        log.info("DASHBOARD | Fetching services health");

        List<MonitoredService> services = serviceRepository.findAll();

        List<ServiceHealthResponse> response = services.stream()
                .map(service -> {

                    Optional<HealthMetric> latestMetricOpt =
                            healthMetricRepository.findTopByServiceIdOrderByRecordedAtDesc(service.getId());

                    if (latestMetricOpt.isEmpty()) {
                        return new ServiceHealthResponse(
                                service.getId(),
                                service.getName(),
                                null,
                                null,
                                null,
                                "UNKNOWN"
                        );
                    }

                    HealthMetric metric = latestMetricOpt.get();

                    boolean isUnhealthy =
                            metric.getAvailabilityStatus().name().equals("DOWN") ||
                            metric.getCpuUsage() > 85 ||
                            metric.getResponseTimeMs() > 2000;

                    String healthState = isUnhealthy ? "UNHEALTHY" : "HEALTHY";

                    return new ServiceHealthResponse(
                            service.getId(),
                            service.getName(),
                            metric.getAvailabilityStatus(),
                            metric.getCpuUsage(),
                            metric.getResponseTimeMs(),
                            healthState
                    );
                })
                .toList();

        log.info("DASHBOARD | Services health computed: {}", response.size());

        return response;
    }
}