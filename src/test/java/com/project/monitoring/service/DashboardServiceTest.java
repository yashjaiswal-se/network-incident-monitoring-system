package com.project.monitoring.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.monitoring.dto.dashboard.DashboardSummaryResponse;
import com.project.monitoring.dto.dashboard.ServiceHealthResponse;
import com.project.monitoring.entity.HealthMetric;
import com.project.monitoring.entity.MonitoredService;
import com.project.monitoring.enums.AvailabilityStatus;
import com.project.monitoring.enums.EnvironmentType;
import com.project.monitoring.enums.ServiceStatus;
import com.project.monitoring.enums.ServiceType;
import com.project.monitoring.repository.AlertRepository;
import com.project.monitoring.repository.HealthMetricRepository;
import com.project.monitoring.repository.IncidentRepository;
import com.project.monitoring.repository.MonitoredServiceRepository;

@SpringBootTest
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private MonitoredServiceRepository serviceRepository;

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private HealthMetricRepository healthMetricRepository;

    @Test
    void shouldReturnDashboardSummary() {
        MonitoredService service = new MonitoredService();
        service.setName("Test Service");
        service.setStatus(ServiceStatus.ACTIVE);
        service.setEnvironment(EnvironmentType.DEV);
        service.setType(ServiceType.APPLICATION);
        service.setIpAddress("127.0.0.1");

        serviceRepository.save(service);

        DashboardSummaryResponse summary = dashboardService.getSummary();

        assertNotNull(summary);
        assertTrue(summary.getTotalServices() >= 1);
        assertTrue(summary.getActiveServices() >= 1);
    }

    @Test
    void shouldMarkServiceAsUnknownWhenNoMetrics() {
        MonitoredService service = new MonitoredService();
        service.setName("Service-No-Metrics");
        service.setStatus(ServiceStatus.ACTIVE);
        service.setEnvironment(EnvironmentType.DEV);
        service.setType(ServiceType.APPLICATION);
        service.setIpAddress("127.0.0.1");

        MonitoredService savedService = serviceRepository.save(service);

        List<ServiceHealthResponse> result = dashboardService.getServicesHealth();

        assertNotNull(result);

        assertTrue(result.stream()
                .anyMatch(r -> r.getServiceId().equals(savedService.getId())
                        && r.getHealthState().equals("UNKNOWN")));
    }

    @Test
    void shouldMarkServiceUnhealthyWhenCpuHigh() {
        MonitoredService service = new MonitoredService();
        service.setName("High-CPU-Service");
        service.setStatus(ServiceStatus.ACTIVE);
        service.setEnvironment(EnvironmentType.DEV);
        service.setType(ServiceType.APPLICATION);
        service.setIpAddress("127.0.0.1");

        MonitoredService savedService = serviceRepository.save(service);

        HealthMetric metric = new HealthMetric();
        metric.setService(savedService);
        metric.setCpuUsage(95.0);
        metric.setResponseTimeMs(100.0);
        metric.setAvailabilityStatus(AvailabilityStatus.UP);
        metric.setLatencyMs(50.0);
        metric.setMemoryUsage(60.0);
        metric.setPacketLoss(0.1);
        metric.setRecordedAt(LocalDateTime.now());

        healthMetricRepository.save(metric);

        List<ServiceHealthResponse> result = dashboardService.getServicesHealth();

        assertNotNull(result);

        assertTrue(result.stream()
                .anyMatch(r -> r.getServiceId().equals(savedService.getId())
                        && r.getHealthState().equals("UNHEALTHY")));
    }

    @Test
    void shouldMarkServiceUnhealthyWhenDown() {
        MonitoredService service = new MonitoredService();
        service.setName("Down-Service");
        service.setStatus(ServiceStatus.ACTIVE);
        service.setEnvironment(EnvironmentType.DEV);
        service.setType(ServiceType.APPLICATION);
        service.setIpAddress("127.0.0.1");

        MonitoredService savedService = serviceRepository.save(service);

        HealthMetric metric = new HealthMetric();
        metric.setService(savedService);
        metric.setCpuUsage(20.0);
        metric.setResponseTimeMs(100.0);
        metric.setAvailabilityStatus(AvailabilityStatus.DOWN);
        metric.setLatencyMs(50.0);
        metric.setMemoryUsage(60.0);
        metric.setPacketLoss(0.1);
        metric.setRecordedAt(LocalDateTime.now());

        healthMetricRepository.save(metric);

        List<ServiceHealthResponse> result = dashboardService.getServicesHealth();

        assertNotNull(result);

        assertTrue(result.stream()
                .anyMatch(r -> r.getServiceId().equals(savedService.getId())
                        && r.getHealthState().equals("UNHEALTHY")));
    }
}