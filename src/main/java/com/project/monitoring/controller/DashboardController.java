package com.project.monitoring.controller;

import com.project.monitoring.dto.dashboard.DashboardSummaryResponse;
import com.project.monitoring.dto.dashboard.ServiceHealthResponse;
import com.project.monitoring.dto.incident.IncidentResponse;
import com.project.monitoring.service.DashboardService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary() {
        return dashboardService.getSummary();
    }
    @GetMapping("/active-incidents")
    public List<IncidentResponse> getActiveIncidents() {
        return dashboardService.getActiveIncidents();
    }
    @GetMapping("/services-health")
    public List<ServiceHealthResponse> getServicesHealth() {
        return dashboardService.getServicesHealth();
    }
}