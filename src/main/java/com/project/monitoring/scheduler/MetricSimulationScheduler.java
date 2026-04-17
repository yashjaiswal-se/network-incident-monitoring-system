package com.project.monitoring.scheduler;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.project.monitoring.dto.metrics.CreateHealthMetricRequest;
import com.project.monitoring.entity.MonitoredService;
import com.project.monitoring.enums.AvailabilityStatus;
import com.project.monitoring.service.HealthMetricsService;
import com.project.monitoring.service.MonitoredServiceService;

@Component
@RequiredArgsConstructor
public class MetricSimulationScheduler {

    private static final Logger log = LoggerFactory.getLogger(MetricSimulationScheduler.class);

    private final MonitoredServiceService monitoredServiceService;
    private final HealthMetricsService healthMetricsService;

    private final Random random = new Random();

    private boolean failureMode = false;
    private int failureCyclesRemaining = 0;

    // Runs every 10 seconds
    @Scheduled(fixedRate = 10000)
    public void simulateMetrics() {

        log.info("Simulation started");

        if (!failureMode && random.nextInt(10) > 7) {
            failureMode = true;
            failureCyclesRemaining = 3 + random.nextInt(3);
            log.warn("Failure mode activated for {} cycles", failureCyclesRemaining);
        }

        List<MonitoredService> services = monitoredServiceService.getAllServiceEntities();

        if (services.isEmpty()) {
            log.warn("No monitored services found. Skipping simulation.");
            return;
        }

        for (MonitoredService service : services) {
            try {
                CreateHealthMetricRequest request = buildMetric(service);
                healthMetricsService.submitMetric(request);

                log.info("Metrics simulated | serviceId={} | serviceName={}",
                        service.getId(), service.getName());

            } catch (Exception ex) {
                log.error("Error simulating metrics for serviceId={}",
                        service.getId(), ex);
            }
        }

        if (failureMode) {
            failureCyclesRemaining--;
            if (failureCyclesRemaining <= 0) {
                failureMode = false;
                log.info("System recovered");
            }
        }

        log.info("Simulation completed");
    }

    private CreateHealthMetricRequest buildMetric(MonitoredService service) {
        CreateHealthMetricRequest request = new CreateHealthMetricRequest();

        request.setServiceId(service.getId());
        request.setCpuUsage(generateCpu());
        request.setMemoryUsage(generateMemory());
        request.setResponseTimeMs(generateResponseTime());
        request.setLatencyMs(generateLatency());
        request.setPacketLoss(generatePacketLoss());
        request.setAvailabilityStatus(generateAvailability());

        return request;
    }

    private Double generateCpu() {
        return failureMode
                ? 85 + random.nextDouble() * 20
                : 40 + random.nextDouble() * 30;
    }

    private Double generateMemory() {
        return 40 + random.nextDouble() * 50;
    }

    private Double generateResponseTime() {
        return failureMode
                ? 2000 + random.nextDouble() * 2000
                : 200 + random.nextDouble() * 800;
    }

    private Double generateLatency() {
        return 50 + random.nextDouble() * 400;
    }

    private Double generatePacketLoss() {
        return random.nextDouble() * 10;
    }

    private AvailabilityStatus generateAvailability() {
        return failureMode
                ? AvailabilityStatus.DOWN
                : (random.nextInt(10) > 7 ? AvailabilityStatus.DOWN : AvailabilityStatus.UP);
    }
}