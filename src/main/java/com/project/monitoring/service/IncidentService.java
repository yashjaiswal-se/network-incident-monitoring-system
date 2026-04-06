package com.project.monitoring.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.monitoring.dto.incident.IncidentResponse;
import com.project.monitoring.entity.Incident;
import com.project.monitoring.entity.MonitoredService;
import com.project.monitoring.entity.MonitoringRule;
import com.project.monitoring.enums.IncidentStatus;
import com.project.monitoring.enums.SeverityLevel;
import com.project.monitoring.exception.ResourceNotFoundException;
import com.project.monitoring.repository.IncidentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final AlertService alertService;

    @Transactional(readOnly = true)
    public List<IncidentResponse> getAllIncidents() {
        return incidentRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IncidentResponse getIncidentById(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));

        return mapToResponse(incident);
    }

    @Transactional
    public IncidentResponse acknowledgeIncident(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));

        if (incident.getStatus() != IncidentStatus.OPEN) {
            throw new IllegalStateException("Only OPEN incidents can be acknowledged");
        }

        incident.setStatus(IncidentStatus.ACKNOWLEDGED);
        incident.setAcknowledgedAt(LocalDateTime.now());

        Incident savedIncident = incidentRepository.save(incident);

        log.info("Incident acknowledged: id={}", savedIncident.getId());

        return mapToResponse(savedIncident);
    }

    @Transactional
    public IncidentResponse resolveIncident(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));

        if (incident.getStatus() == IncidentStatus.RESOLVED) {
            throw new IllegalStateException("Incident is already resolved");
        }

        incident.setStatus(IncidentStatus.RESOLVED);
        incident.setResolvedAt(LocalDateTime.now());

        Incident savedIncident = incidentRepository.save(incident);

        log.info("Incident resolved: id={}", savedIncident.getId());

        return mapToResponse(savedIncident);
    }

    @Transactional
    public void createIncidentIfNeeded(MonitoredService service, List<MonitoringRule> violatedRules) {
        if (violatedRules == null || violatedRules.isEmpty()) {
            return;
        }

        boolean hasOpenIncident = incidentRepository.existsByServiceIdAndStatus(service.getId(), IncidentStatus.OPEN);
        boolean hasAcknowledgedIncident = incidentRepository.existsByServiceIdAndStatus(service.getId(), IncidentStatus.ACKNOWLEDGED);

        if (hasOpenIncident || hasAcknowledgedIncident) {
            log.warn("Skipping incident creation. Active incident already exists for serviceId={}", service.getId());
            return;
        }

        SeverityLevel highestSeverity = getHighestSeverity(violatedRules);

        String title = service.getName() + " Health Degradation Detected";

        String description = service.getName() + " violated multiple monitoring thresholds including: " +
                violatedRules.stream()
                        .map(rule -> rule.getMetricName().name())
                        .distinct()
                        .collect(Collectors.joining(", "));

        Incident incident = Incident.builder()
                .service(service)
                .title(title)
                .description(description)
                .severity(highestSeverity)
                .status(IncidentStatus.OPEN)
                .build();

        Incident savedIncident = incidentRepository.save(incident);

        log.warn("Incident created for serviceId={}, incidentId={}, severity={}",
                service.getId(), savedIncident.getId(), savedIncident.getSeverity());

        alertService.sendAlert(savedIncident);
    }

    private SeverityLevel getHighestSeverity(List<MonitoringRule> violatedRules) {
        return violatedRules.stream()
                .map(MonitoringRule::getSeverity)
                .max(Comparator.comparingInt(this::severityRank))
                .orElse(SeverityLevel.LOW);
    }

    private int severityRank(SeverityLevel severity) {
        return switch (severity) {
            case LOW -> 1;
            case MEDIUM -> 2;
            case HIGH -> 3;
            case CRITICAL -> 4;
        };
    }

    private IncidentResponse mapToResponse(Incident incident) {
        return IncidentResponse.builder()
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
                .build();
    }
}