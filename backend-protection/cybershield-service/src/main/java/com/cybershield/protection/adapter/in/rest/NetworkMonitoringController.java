package com.cybershield.protection.adapter.in.rest;

import com.cybershield.protection.adapter.in.rest.dto.NetworkTrafficRequest;
import com.cybershield.protection.adapter.in.rest.dto.NetworkTrafficResponse;
import com.cybershield.protection.core.dto.GlobalDeviceDashboardResponse;
import com.cybershield.protection.core.port.in.RecordNetworkTrafficUseCase;
import com.cybershield.protection.core.usecase.NetworkMetricService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/network-monitoring")
public class NetworkMonitoringController {

    private final RecordNetworkTrafficUseCase trafficUseCase;
    private static final long SEUIL_CRITIQUE = 500_000_000L; // 500 Mo
    private static final long SEUIL_WARNING = 50_000_000L;  // 50 Mo
    private final NetworkMetricService networkMetricService;

    public NetworkMonitoringController(RecordNetworkTrafficUseCase trafficUseCase,
                                       NetworkMetricService networkMetricService) {
        this.trafficUseCase = trafficUseCase;
        this.networkMetricService = networkMetricService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<List<GlobalDeviceDashboardResponse>> getDashboard() {
        // Maintenant networkMetricService est reconnu !
        return ResponseEntity.ok(networkMetricService.getGlobalDashboard());
    }

    @PostMapping
    public ResponseEntity<NetworkTrafficResponse> reportTraffic(@Valid @RequestBody NetworkTrafficRequest request) {

        // 1. Enregistrement
        trafficUseCase.record(request.deviceId(), request.bytesSent(), request.bytesReceived());

        // 2. Logique de décision
        double total = request.bytesSent() + request.bytesReceived();
        String status;
        int statusCode; // 1: CRITICAL, 2: WARNING, 3: SECURE
        String message;

        if (total > SEUIL_CRITIQUE) {
            status = "CRITICAL";
            statusCode = 1;
            message = "ALERTE : Volume de données anormal détecté (> 500Mo).";
        } else if (total > SEUIL_WARNING) {
            status = "WARNING";
            statusCode = 2;
            message = "ATTENTION : Utilisation importante de la bande passante.";
        } else {
            status = "SECURE";
            statusCode = 3;
            message = "Trafic réseau nominal.";
        }

        // 3. Réponse JSON
        NetworkTrafficResponse response = new NetworkTrafficResponse(
                request.deviceId(),
                status,
                statusCode, // Ton nouveau champ integer
                total,
                message
        );

        return ResponseEntity.ok(response);
    }
}