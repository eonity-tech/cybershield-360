package com.cybershield.protection.adapter.out.persistence.network;

import com.cybershield.protection.core.domain.NetworkMetric;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "network_metrics")
public class NetworkMetricEntity {

    @Id
    private UUID id;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Column(name = "bytes_sent")
    private double bytesSent; // Upload

    @Column(name = "bytes_received")
    private double bytesReceived; // Download

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // --- CONSTRUCTEUR PAR DÉFAUT ---
    public NetworkMetricEntity() {}

    // --- GETTERS ET SETTERS (AJOUTÉS MANUELLEMENT) ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getDeviceId() { return deviceId; }
    public void setDeviceId(UUID deviceId) { this.deviceId = deviceId; }

    public double getBytesSent() { return bytesSent; }
    public void setBytesSent(double bytesSent) { this.bytesSent = bytesSent; }

    public double getBytesReceived() { return bytesReceived; }
    public void setBytesReceived(double bytesReceived) { this.bytesReceived = bytesReceived; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    // --- MAPPERS ---

    // Mapper : du Domaine vers l'Entité
    public static NetworkMetricEntity fromDomain(NetworkMetric domain) {
        NetworkMetricEntity entity = new NetworkMetricEntity();
        entity.setId(domain.getId());
        entity.setDeviceId(domain.getDeviceId());
        entity.setBytesSent(domain.getBytesSent());
        entity.setBytesReceived(domain.getBytesReceived());
        entity.setTimestamp(domain.getTimestamp());
        return entity;
    }

    // Mapper : de l'Entité vers le Domaine
    public NetworkMetric toDomain() {
        return new NetworkMetric(
                this.id,
                this.deviceId,
                this.bytesSent,
                this.bytesReceived,
                this.timestamp
        );
    }

    public void updateTraffic(Double bytesSent, Double bytesReceived) {
        this.bytesSent = bytesSent;
        this.bytesReceived = bytesReceived;
        this.timestamp = LocalDateTime.now();
    }
}