package com.cybershield.protection.core.dto; // Déplacé dans le Core

import java.util.List;
import java.util.UUID;

public record GlobalDeviceDashboardResponse(
        UUID deviceId,
        String ipAddress,
        String hostname,
        List<String> detectedSoftwares,
        double currentUsage,
        int statusCode, // 1: CRITICAL, 2: WARNING, 3: SECURE
        String message
) {}