package com.cybershield.protection.adapter.in.rest.dto.global;

import java.util.UUID;

/**
 * Réponse unifiée envoyée à la sonde après une synchronisation complète.
 */
public record GlobalStatusResponse(
        UUID deviceId,
        String status,
        String message,
        String securityRecommendation,
        String vulnerabilityLevel
) {}