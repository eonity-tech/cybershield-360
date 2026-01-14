package com.cybershield.protection.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record NetworkTrafficRequest(
        @NotNull(message = "L'ID de l'appareil est requis")
        UUID deviceId,

        @NotNull
        Double bytesSent,     // Quantité d'octets envoyés (Upload)

        @NotNull
        Double bytesReceived  // Quantité d'octets reçus (Download)
) {}