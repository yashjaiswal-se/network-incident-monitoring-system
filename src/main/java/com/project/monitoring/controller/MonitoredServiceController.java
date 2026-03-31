package com.project.monitoring.controller;

import com.project.monitoring.dto.service.CreateServiceRequest;
import com.project.monitoring.dto.service.ServiceResponse;
import com.project.monitoring.dto.service.UpdateServiceRequest;
import com.project.monitoring.service.MonitoredServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class MonitoredServiceController {

    private final MonitoredServiceService service;

    // CREATE SERVICE
    @PostMapping
    public ServiceResponse createService(@Valid @RequestBody CreateServiceRequest request) {
        return service.createService(request);
    }

    // GET ALL SERVICES
    @GetMapping
    public List<ServiceResponse> getAllServices() {
        return service.getAllServices();
    }

    // GET SERVICE BY ID
    @GetMapping("/{id}")
    public ServiceResponse getServiceById(@PathVariable Long id) {
        return service.getServiceById(id);
    }

    // UPDATE SERVICE
    @PutMapping("/{id}")
    public ServiceResponse updateService(@PathVariable Long id,
                                         @Valid @RequestBody UpdateServiceRequest request) {
        return service.updateService(id, request);
    }
}