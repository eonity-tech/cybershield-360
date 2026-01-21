package com.cybershield.protection.adapter.in.rest.dto.global;

import java.util.List;
import java.util.UUID;

/**
 * Ce DTO est NOUVEAU. Il sert uniquement pour le GET /full-report.
 */
public record GlobalDashboardResponse(
        int totalDevices,
        int protectedDevices,
        int compromisedDevices,
        List<DeviceSummary> devices
) {
    // Sous-objet pour la liste (plus léger que l'objet complet)
    public record DeviceSummary(
            UUID id,
            String hostname,
            String ipAddress,
            String osVersion,
            String status,
            double riskScore,
            String securityRecommendation
    ) {}
}