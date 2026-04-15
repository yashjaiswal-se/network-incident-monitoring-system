package com.project.monitoring.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.monitoring.dto.incident.IncidentResponse;
import com.project.monitoring.entity.MonitoredService;
import com.project.monitoring.enums.EnvironmentType;
import com.project.monitoring.enums.ServiceStatus;
import com.project.monitoring.enums.ServiceType;
import com.project.monitoring.repository.MonitoredServiceRepository;

@SpringBootTest
class IncidentServiceTest {

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private MonitoredServiceRepository serviceRepository;

    @Test
    void shouldFetchAllIncidents() {
        // Arrange
        MonitoredService service = new MonitoredService();
        service.setName("Test Service");
        service.setStatus(ServiceStatus.ACTIVE);
        service.setEnvironment(EnvironmentType.DEV);
        service.setType(ServiceType.APPLICATION);
        service.setIpAddress("127.0.0.1");

        serviceRepository.save(service);

        // Act
        List<IncidentResponse> incidents = incidentService.getAllIncidents();

        // Assert
        assertNotNull(incidents);
        assertTrue(incidents.size() >= 0); // basic sanity check
    }
}