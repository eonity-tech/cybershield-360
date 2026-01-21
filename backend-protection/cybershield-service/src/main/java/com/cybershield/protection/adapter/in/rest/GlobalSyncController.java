package com.cybershield.protection.adapter.in.rest;

import com.cybershield.protection.adapter.in.rest.dto.global.GlobalEnrollmentRequest;
import com.cybershield.protection.adapter.in.rest.dto.global.GlobalStatusResponse;
import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.port.in.EnrollDeviceUseCase;
import com.cybershield.protection.core.port.in.RecordNetworkTrafficUseCase;
import com.cybershield.protection.core.port.in.RegisterSoftwareUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sync")
public class GlobalSyncController {

    private final EnrollDeviceUseCase enrollDeviceUseCase;
    private final RegisterSoftwareUseCase softwareUseCase;
    private final RecordNetworkTrafficUseCase trafficUseCase;

    public GlobalSyncController(EnrollDeviceUseCase enrollDeviceUseCase, RegisterSoftwareUseCase softwareUseCase, RecordNetworkTrafficUseCase trafficUseCase) {
        this.enrollDeviceUseCase = enrollDeviceUseCase;
        this.softwareUseCase = softwareUseCase;
        this.trafficUseCase = trafficUseCase;
    }

    @PostMapping("/full-report")
    public ResponseEntity<GlobalStatusResponse> syncAll(@Valid @RequestBody GlobalEnrollmentRequest request) {

        // 1. Appel du service d'enrôlement avec les données du DTO
        // On extrait chaque champ de request.device()
        Device device = enrollDeviceUseCase.enroll(
                request.device().macAddress(),
                request.device().ipAddress(),
                request.device().type(),
                request.device().osType(),
                request.device().osVersion(),
                request.device().hostname(),
                request.device().vendor(),
                request.device().ttl(),
                request.device().openPorts()
        );

        // 2. Enregistrement des logiciels
        // On utilise l'ID du device que l'on vient de créer
        request.softwares().forEach(sw -> softwareUseCase.register(
                device.getId(),
                sw.name(),
                sw.version(),
                sw.publisher(),
                sw.isRunning()
        ));

        // 3. Premier rapport réseau
        trafficUseCase.record(
                device.getId(),
                request.initialTraffic().bytesSent(),
                request.initialTraffic().bytesReceived()
        );

        // 4. Retour de la réponse
        return ResponseEntity.ok(new GlobalStatusResponse(
                device.getId(),
                "SYNC_SUCCESS",
                "Synchronisation terminée avec succès",
                device.getSecurityRecommendation(),
                device.getVulnerabilityLevel().name()
        ));
    }
}