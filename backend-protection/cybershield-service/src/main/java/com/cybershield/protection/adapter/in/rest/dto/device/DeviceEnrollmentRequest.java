package com.cybershield.protection.adapter.in.rest.dto.device;

import com.cybershield.protection.core.domain.type.DeviceType;
import com.cybershield.protection.core.domain.type.OsType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DeviceEnrollmentRequest(
        @NotBlank(message = "L'adresse MAC est obligatoire")
        @Pattern(regexp = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$", message = "Format MAC invalide")
        String macAddress,

        @NotBlank(message = "L'adresse IP est obligatoire")
        String ipAddress,

        @NotNull(message = "Le type d'appareil est obligatoire (WORKSTATION, SERVER, IOT_DEVICE...)")
        DeviceType type,

        @NotNull(message = "L'OS est obligatoire")
        OsType osType,

        @NotBlank(message = "La version de l'OS est obligatoire")
        String osVersion,

        // --- Champs optionnels pour le Scan Python (Scapy) ---
        String hostname,

        String vendor,

        @Min(0) @Max(255)
        Integer ttl,

        String openPorts
) {}