package com.cybershield.protection.adapter.in.rest.dto.software;

import com.cybershield.protection.core.domain.Software;
import java.util.UUID;

public record SoftwareResponse(
        UUID id,
        UUID deviceId,
        String name,
        String version,
        String publisher,
        boolean isRunning,
        Double criticalScore,
        String riskLevel,
        String recommendation
) {
    public static SoftwareResponse fromDomain(Software software) {
        return new SoftwareResponse(
                software.getId(),
                software.getDeviceId(),
                software.getName(),
                software.getVersion(),
                software.getPublisher(),
                software.isRunning(),
                software.getCriticalScore(),
                determineRiskLevel(software.getCriticalScore()),
                software.getSecurityRecommendation()
        );
    }

    private static String determineRiskLevel(Double score) {
        if (score >= 80) return "CRITIQUE";
        if (score >= 40) return "MODÉRÉ";
        if (score > 0) return "FAIBLE";
        return "SAIN";
    }
}