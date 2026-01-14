package com.cybershield.protection.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO envoyé par la sonde Python (psutil) pour déclarer un logiciel ou un processus.
 */
public record SoftwareInclusionRequest(
        @NotNull(message = "L'ID de l'appareil est obligatoire pour rattacher le logiciel")
        UUID deviceId,

        @NotBlank(message = "Le nom du logiciel est obligatoire")
        String name,

        @NotBlank(message = "La version est nécessaire pour l'analyse des failles")
        String version,

        String publisher,

        @NotNull(message = "Le statut d'exécution (vrai/faux) est obligatoire")
        boolean isRunning
) {}