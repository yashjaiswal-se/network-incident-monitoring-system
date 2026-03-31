package com.project.monitoring.dto.service;

import com.project.monitoring.enums.EnvironmentType;
import com.project.monitoring.enums.ServiceStatus;
import com.project.monitoring.enums.ServiceType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ServiceResponse {

    private Long id;
    private String name;
    private ServiceType type;
    private String ipAddress;
    private EnvironmentType environment;
    private ServiceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}