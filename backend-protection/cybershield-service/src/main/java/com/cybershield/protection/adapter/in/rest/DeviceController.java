package com.cybershield.protection.adapter.in.rest;

import com.cybershield.protection.adapter.in.rest.dto.device.DeviceEnrollmentRequest;
import com.cybershield.protection.adapter.in.rest.dto.device.DeviceResponse;
import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.port.in.EnrollDeviceUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.stream.Collectors; // ✅ Import indispensable

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

    private final EnrollDeviceUseCase enrollDeviceUseCase;

    public DeviceController(EnrollDeviceUseCase enrollDeviceUseCase) {
        this.enrollDeviceUseCase = enrollDeviceUseCase;
    }

    @PostMapping
    public ResponseEntity<DeviceResponse> enroll(
            @Valid @RequestBody DeviceEnrollmentRequest request
    ) {
        // 1. Conversion de la liste des ports ouverts en chaîne de caractères
        String portsAsString = "";
        if (request.openPorts() != null && !request.openPorts().isEmpty()) {
            portsAsString = request.openPorts().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }

        // 2. UseCase d'enrôlement du Device
        Device domainDevice = enrollDeviceUseCase.enroll(
                request.macAddress(),
                request.ipAddress(),
                request.type(),
                request.osType(),
                request.osVersion(),
                request.hostname(),
                request.vendor(),
                request.ttl(),
                portsAsString
        );

        // 3. Conversion Domaine -> DTO Réponse
        DeviceResponse response = DeviceResponse.fromDomain(domainDevice);

        return ResponseEntity.status(201).body(response);
    }

    // 4. Nouvelle méthode pour bloquer un Device (Phase 6)
    @PostMapping("/security/{deviceId}/block")
    public Mono<ResponseEntity<Void>> blockDevice(@PathVariable("deviceId") UUID deviceId) {
        // Appel au UseCase du Core pour marquer le device en blacklist et notifier Redis [cite: 341, 343]
        return Mono.fromRunnable(() -> enrollDeviceUseCase.blockDevice(deviceId))
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    // Méthode pour débloquer un Device
    @PostMapping("/security/{deviceId}/unblock")
    public Mono<ResponseEntity<Void>> unblockDevice(@PathVariable("deviceId") UUID deviceId) {
        return Mono.fromRunnable(() -> enrollDeviceUseCase.unblockDevice(deviceId))
                .then(Mono.just(ResponseEntity.ok().build()));
    }
}