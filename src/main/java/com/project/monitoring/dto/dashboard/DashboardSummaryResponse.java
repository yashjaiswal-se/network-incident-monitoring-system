package com.project.monitoring.dto.dashboard;

public class DashboardSummaryResponse {

    private final long totalServices;
    private final long activeServices;
    private final long totalIncidents;
    private final long activeIncidents;
    private final long totalAlerts;

    public DashboardSummaryResponse(long totalServices,
                                    long activeServices,
                                    long totalIncidents,
                                    long activeIncidents,
                                    long totalAlerts) {
        this.totalServices = totalServices;
        this.activeServices = activeServices;
        this.totalIncidents = totalIncidents;
        this.activeIncidents = activeIncidents;
        this.totalAlerts = totalAlerts;
    }

    public long getTotalServices() {
        return totalServices;
    }

    public long getActiveServices() {
        return activeServices;
    }

    public long getTotalIncidents() {
        return totalIncidents;
    }

    public long getActiveIncidents() {
        return activeIncidents;
    }

    public long getTotalAlerts() {
        return totalAlerts;
    }
}