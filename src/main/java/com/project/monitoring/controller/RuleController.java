package com.project.monitoring.controller;

import com.project.monitoring.dto.rule.CreateMonitoringRuleRequest;
import com.project.monitoring.dto.rule.MonitoringRuleResponse;
import com.project.monitoring.service.MonitoringRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class RuleController {

    private final MonitoringRuleService monitoringRuleService;

    @PostMapping
    public MonitoringRuleResponse createRule(@Valid @RequestBody CreateMonitoringRuleRequest request) {
        return monitoringRuleService.createRule(request);
    }

    @GetMapping
    public List<MonitoringRuleResponse> getAllRules() {
        return monitoringRuleService.getAllRules();
    }

    @PatchMapping("/{id}/status")
    public MonitoringRuleResponse toggleRuleStatus(@PathVariable Long id) {
        return monitoringRuleService.toggleRuleStatus(id);
    }
}