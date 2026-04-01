package com.project.monitoring.controller;

import com.project.monitoring.dto.metrics.CreateHealthMetricRequest;
import com.project.monitoring.dto.metrics.HealthMetricResponse;
import com.project.monitoring.service.HealthMetricsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final HealthMetricsService healthMetricsService;

    @PostMapping
    public HealthMetricResponse submitMetric(@Valid @RequestBody CreateHealthMetricRequest request) {
        return healthMetricsService.submitMetric(request);
    }

    @GetMapping("/service/{serviceId}")
    public List<HealthMetricResponse> getMetricsByService(@PathVariable Long serviceId) {
        return healthMetricsService.getMetricsByService(serviceId);
    }

    @PostMapping("/simulate/{serviceId}")
    public HealthMetricResponse simulateMetric(@PathVariable Long serviceId) {
        return healthMetricsService.simulateMetric(serviceId);
    }
}