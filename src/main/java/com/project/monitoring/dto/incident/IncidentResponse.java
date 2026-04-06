package com.project.monitoring.dto.incident;

import java.time.LocalDateTime;

import com.project.monitoring.enums.IncidentStatus;
import com.project.monitoring.enums.SeverityLevel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentResponse {
    
private Long id;
private Long serviceId;
private String serviceName;
private String title;
private String description;
private SeverityLevel severity;
private IncidentStatus status;
private LocalDateTime createdAt;
private LocalDateTime acknowledgedAt;
private LocalDateTime resolvedAt;
    
}
