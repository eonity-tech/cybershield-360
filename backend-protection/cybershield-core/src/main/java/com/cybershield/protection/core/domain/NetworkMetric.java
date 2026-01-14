package com.cybershield.protection.core.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class NetworkMetric {
    private final UUID id;
    private final UUID deviceId;
    private final double bytesSent;     // Upload
    private final double bytesReceived; // Download
    private final LocalDateTime timestamp;

    public NetworkMetric(UUID id, UUID deviceId, double bytesSent, double bytesReceived) {
        this.id = id;
        this.deviceId = deviceId;
        this.bytesSent = bytesSent;
        this.bytesReceived = bytesReceived;
        this.timestamp = LocalDateTime.now();
    }

    // --- GETTERS (Nécessaires pour le mapping JPA) ---

    public UUID getId() {
        return id;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public double getBytesSent() {
        return bytesSent;
    }

    public double getBytesReceived() {
        return bytesReceived;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}