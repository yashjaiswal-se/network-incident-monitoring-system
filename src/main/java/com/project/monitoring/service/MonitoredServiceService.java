package com.project.monitoring.service;

import com.project.monitoring.dto.service.CreateServiceRequest;
import com.project.monitoring.dto.service.ServiceResponse;
import com.project.monitoring.dto.service.UpdateServiceRequest;
import com.project.monitoring.entity.MonitoredService;
import com.project.monitoring.repository.MonitoredServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitoredServiceService {

    private final MonitoredServiceRepository repository;

    // CREATE
    public ServiceResponse createService(CreateServiceRequest request) {

        MonitoredService service = MonitoredService.builder()
                .name(request.getName())
                .type(request.getType())
                .ipAddress(request.getIpAddress())
                .environment(request.getEnvironment())
                .status(request.getStatus())
                .build();

        MonitoredService saved = repository.save(service);

        return mapToResponse(saved);
    }

    // GET ALL
    public List<ServiceResponse> getAllServices() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // GET BY ID
    public ServiceResponse getServiceById(Long id) {
        MonitoredService service = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        return mapToResponse(service);
    }

    // UPDATE
    public ServiceResponse updateService(Long id, UpdateServiceRequest request) {

        MonitoredService service = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        service.setName(request.getName());
        service.setType(request.getType());
        service.setIpAddress(request.getIpAddress());
        service.setEnvironment(request.getEnvironment());
        service.setStatus(request.getStatus());

        MonitoredService updated = repository.save(service);

        return mapToResponse(updated);
    }

    // MAPPER
    private ServiceResponse mapToResponse(MonitoredService service) {
        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .type(service.getType())
                .ipAddress(service.getIpAddress())
                .environment(service.getEnvironment())
                .status(service.getStatus())
                .createdAt(service.getCreatedAt())
                .updatedAt(service.getUpdatedAt())
                .build();
    }
}