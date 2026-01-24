package com.cybershield.protection.adapter.in.rest;

import com.cybershield.protection.adapter.in.rest.dto.software.SoftwareInclusionRequest;
import com.cybershield.protection.adapter.in.rest.dto.software.SoftwareResponse;
import com.cybershield.protection.core.domain.Software;
import com.cybershield.protection.core.port.in.RegisterSoftwareUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/softwares")
public class SoftwareController {
    private final RegisterSoftwareUseCase softwareUseCase;

    public SoftwareController(RegisterSoftwareUseCase softwareUseCase) {
        this.softwareUseCase = softwareUseCase;
    }

    @PostMapping
    public ResponseEntity<SoftwareResponse> register(@Valid @RequestBody SoftwareInclusionRequest request) {

        // verification que l'ID du device est present
        if (request.deviceId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'ID du device est obligatoire pour cet endpoint.");
        }

        Software sw = softwareUseCase.register(
                request.deviceId(),
                request.name(),
                request.version(),
                request.type(),
                request.publisher(),
                request.isRunning()
        );
        return ResponseEntity.status(201).body(SoftwareResponse.fromDomain(sw));
    }
}