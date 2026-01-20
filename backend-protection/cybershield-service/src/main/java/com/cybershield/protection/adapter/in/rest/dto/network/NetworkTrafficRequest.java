package com.cybershield.protection.adapter.in.rest.dto.network;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record NetworkTrafficRequest(
        UUID deviceId,

        @NotNull
        Double bytesSent,     // Quantité d'octets envoyés (Upload)

        @NotNull
        Double bytesReceived  // Quantité d'octets reçus (Download)
) {}