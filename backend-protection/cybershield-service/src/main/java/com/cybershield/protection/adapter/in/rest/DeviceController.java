package com.cybershield.protection.adapter.in.rest;

import com.cybershield.protection.adapter.in.rest.dto.device.DeviceEnrollmentRequest;
import com.cybershield.protection.adapter.in.rest.dto.device.DeviceResponse;
import com.cybershield.protection.adapter.infrastructure.jwt.ConnectedUser;
import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.port.in.EnrollDeviceUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

    private final EnrollDeviceUseCase enrollDeviceUseCase;

    public DeviceController(EnrollDeviceUseCase enrollDeviceUseCase) {
        this.enrollDeviceUseCase = enrollDeviceUseCase;
    }

    @PostMapping
    public ResponseEntity<DeviceResponse> enroll(
            @Valid @RequestBody DeviceEnrollmentRequest request,
            ConnectedUser connectedUser
    ) {
        // 1. Appel du Core (Métier) avec TOUS les paramètres
        Device domainDevice = enrollDeviceUseCase.enroll(
                request.macAddress(),
                request.ipAddress(),
                request.type(),
                request.osType(),
                request.osVersion(),
                request.hostname(),
                request.vendor(),
                request.ttl(),
                request.openPorts()
        );

        // 2. Conversion Domaine -> DTO Réponse
        DeviceResponse response = DeviceResponse.fromDomain(domainDevice);

        return ResponseEntity.status(201).body(response);
    }
}