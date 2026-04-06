package com.project.monitoring.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.monitoring.dto.incident.IncidentResponse;
import com.project.monitoring.service.IncidentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @GetMapping
    public ResponseEntity<List<IncidentResponse>> getAllIncidents() {
        return ResponseEntity.ok(incidentService.getAllIncidents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> getIncidentById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getIncidentById(id));
    }

    @PatchMapping("/{id}/acknowledge")
    public ResponseEntity<IncidentResponse> acknowledgeIncident(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.acknowledgeIncident(id));
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<IncidentResponse> resolveIncident(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.resolveIncident(id));
    }
}