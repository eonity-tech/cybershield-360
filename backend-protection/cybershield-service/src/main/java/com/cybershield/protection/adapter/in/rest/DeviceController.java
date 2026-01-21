package com.cybershield.protection.adapter.in.rest;

import com.cybershield.protection.adapter.in.rest.dto.device.DeviceEnrollmentRequest;
import com.cybershield.protection.adapter.in.rest.dto.device.DeviceResponse;
import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.domain.type.DeviceType;
import com.cybershield.protection.core.domain.type.OsType;
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

    //  Endpoint d'enrôlement des dispositifs avec les nouveaux champs
    @PostMapping
    public ResponseEntity<DeviceResponse> enroll(
            @Valid @RequestBody DeviceEnrollmentRequest request
    ) {
        // 1. Conversion DTO -> Domaine et Appel du UseCase
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