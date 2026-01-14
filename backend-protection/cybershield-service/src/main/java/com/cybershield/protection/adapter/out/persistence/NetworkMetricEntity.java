package com.cybershield.protection.adapter.out.persistence;

import com.cybershield.protection.core.domain.NetworkMetric;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "network_metrics")
@Data
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

    // Constructeur par défaut pour JPA
    public NetworkMetricEntity() {}

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
                this.bytesReceived
        );
    }
}