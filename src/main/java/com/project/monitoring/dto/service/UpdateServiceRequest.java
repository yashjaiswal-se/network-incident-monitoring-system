package com.project.monitoring.dto.service;

import com.project.monitoring.enums.EnvironmentType;
import com.project.monitoring.enums.ServiceStatus;
import com.project.monitoring.enums.ServiceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateServiceRequest {

    @NotBlank
    private String name;

    @NotNull
    private ServiceType type;

    @NotBlank
    private String ipAddress;

    @NotNull
    private EnvironmentType environment;

    @NotNull
    private ServiceStatus status;
}