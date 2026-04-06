package com.project.monitoring.service;

import com.project.monitoring.entity.Alert;
import com.project.monitoring.entity.Incident;
import com.project.monitoring.enums.AlertStatus;
import com.project.monitoring.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;

    @Transactional
    public void sendAlert(Incident incident) {
        String message = buildAlertMessage(incident);

        Alert alert = Alert.builder()
                .incident(incident)
                .message(message)
                .status(AlertStatus.SENT)
                .build();

        alertRepository.save(alert);

        log.warn("ALERT SENT | incidentId={} | service={} | severity={} | message={}",
                incident.getId(),
                incident.getService().getName(),
                incident.getSeverity(),
                message);
    }

    private String buildAlertMessage(Incident incident) {
        return String.format(
                "ALERT: [%s] Service '%s' triggered incident '%s'. Description: %s",
                incident.getSeverity(),
                incident.getService().getName(),
                incident.getTitle(),
                incident.getDescription()
        );
    }
}