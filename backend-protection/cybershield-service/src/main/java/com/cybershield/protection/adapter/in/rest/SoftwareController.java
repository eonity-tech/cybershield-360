package com.cybershield.protection.adapter.in.rest;

import com.cybershield.protection.adapter.in.rest.dto.software.SoftwareInclusionRequest;
import com.cybershield.protection.adapter.in.rest.dto.software.SoftwareResponse;
import com.cybershield.protection.core.domain.Software;
import com.cybershield.protection.core.port.in.RegisterSoftwareUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/applications")
public class SoftwareController {
    private final RegisterSoftwareUseCase softwareUseCase;

    public SoftwareController(RegisterSoftwareUseCase softwareUseCase) {
        this.softwareUseCase = softwareUseCase;
    }

    @PostMapping
    public ResponseEntity<SoftwareResponse> register(@Valid @RequestBody SoftwareInclusionRequest request) {
        Software sw = softwareUseCase.register(
                request.deviceId(),
                request.name(),
                request.version(),
                request.publisher(),
                request.isRunning()
        );
        return ResponseEntity.status(201).body(SoftwareResponse.fromDomain(sw));
    }
}