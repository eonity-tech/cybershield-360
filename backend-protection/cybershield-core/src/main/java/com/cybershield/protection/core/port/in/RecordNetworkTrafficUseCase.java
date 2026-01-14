package com.cybershield.protection.core.port.in;

import com.cybershield.protection.core.dto.GlobalDeviceDashboardResponse;

import java.util.List;
import java.util.UUID;

public interface RecordNetworkTrafficUseCase {
    void record(UUID deviceId, double bytesSent, double bytesReceived);
    // On ajoute le contrat ici
    List<GlobalDeviceDashboardResponse> getGlobalDashboard();
}