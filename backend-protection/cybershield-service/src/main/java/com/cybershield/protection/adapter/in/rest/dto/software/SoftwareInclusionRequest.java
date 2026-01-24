package com.cybershield.protection.adapter.in.rest.dto.software;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SoftwareInclusionRequest(
        UUID deviceId,

        @NotBlank(message = "Le nom du logiciel est obligatoire")
        String name,

        @NotBlank(message = "La version est nécessaire")
        String version,

        String type,

        String publisher,

        @NotNull(message = "Le statut d'exécution est obligatoire")
        boolean isRunning
) {}