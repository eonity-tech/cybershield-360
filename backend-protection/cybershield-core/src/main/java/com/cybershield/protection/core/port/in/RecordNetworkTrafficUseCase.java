package com.cybershield.protection.core.port.in;

import com.cybershield.protection.core.model.GlobalDeviceDashboardSummary;

import java.util.List;
import java.util.UUID;

// Enregistrement du trafic réseau
// Respecte la sémantique du DDD (Domain-Driven Design)
public interface RecordNetworkTrafficUseCase {
    void record(UUID deviceId, double bytesSent, double bytesReceived);

    List<GlobalDeviceDashboardSummary> getGlobalDashboard();
}