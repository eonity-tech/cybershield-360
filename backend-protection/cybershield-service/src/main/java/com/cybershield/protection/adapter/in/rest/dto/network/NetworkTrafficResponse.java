package com.cybershield.protection.adapter.in.rest.dto.network;

import java.util.UUID;

public record NetworkTrafficResponse(
        UUID deviceId,
        String status,       // "SECURE", "WARNING", "CRITICAL"
        int statusCode,      // 1: CRITICAL, 2: WARNING, 3: SECURE
        double currentUsage, // Total bytes reçu/envoyé
        String message       // Un message clair pour le "utilisateur"
) {}